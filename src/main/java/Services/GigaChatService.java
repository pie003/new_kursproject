/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

/**
 *
 * @author Ирина
 */
import com.google.gson.*;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import main_object.Excuse.ExcuseParams;
import main_object.User.User;

@Service
public class GigaChatService {
    
    private static final String AUTH_URL = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth";
    private static final String COMPLETION_URL = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions";
    
    private final OkHttpClient client;
    private final Gson gson;
    private String accessToken;
    private long tokenExpiry;
    
    public GigaChatService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    /**
     * Получение токена доступа
     */
    private String getAccessToken() throws IOException {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry) {
            return accessToken;
        }
        
        String apiKey = System.getenv("GIGACHAT_AUTH_KEY");
        Request request = new Request.Builder()
                .url(AUTH_URL)
                .post(RequestBody.create("scope=GIGACHAT_API_PERS", MediaType.parse("application/x-www-form-urlencoded")))
                .header("Authorization", "Bearer " + apiKey)
                .header("RqUID", java.util.UUID.randomUUID().toString())
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            JsonObject json = gson.fromJson(body, JsonObject.class);
            
            if (json.has("access_token")) {
                accessToken = json.get("access_token").getAsString();
                // Токен живёт 30 минут (1800 секунд)
                tokenExpiry = System.currentTimeMillis() + 1800000;
                return accessToken;
            } else {
                throw new IOException("Failed to get token: " + body);
            }
        }
    }
    
    public String sendPrompt(String prompt) {
        try {
            String token = getAccessToken();
            
            // Формируем JSON запрос
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);
            
            JsonArray messages = new JsonArray();
            messages.add(message);
            
            JsonObject requestBody = new JsonObject();
            requestBody.add("messages", messages);
            requestBody.addProperty("model", "GigaChat");
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("max_tokens", 500);
            
            Request request = new Request.Builder()
                    .url(COMPLETION_URL)
                    .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                    .header("Authorization", "Bearer " + token)
                    .build();
            
            try (Response response = client.newCall(request).execute()) {
                String body = response.body().string();
                JsonObject json = gson.fromJson(body, JsonObject.class);
                
                // Парсим ответ
                if (json.has("choices") && json.getAsJsonArray("choices").size() > 0) {
                    return json.getAsJsonArray("choices")
                            .get(0)
                            .getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();
                } else if (json.has("error")) {
                    String error = json.getAsJsonObject("error").get("message").getAsString();
                    throw new IOException("API error: " + error);
                } else {
                    throw new IOException("Unexpected response: " + body);
                }
            }
        } catch (Exception e) {
            System.err.println("GigaChat API error: " + e.getMessage());
            return getFallbackResponse();
        }
    }
    
    public String buildPrompt(ExcuseParams params, User user) {
        String gender = user.getGender();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String group = user.getGroup();
        String recipient = params.getRecipient();
        
        String formalityRu = params.getFormalityLevel().getDescription();
        String urgencyRu = params.getUrgency().getDescription();
        String toneRu = params.getTone().getDescription();
        String lengthRu = params.getLength().getDisplayName();
        

        return String.format("""
            Напиши объяснительное письмо от имени %s %s (группа %s). Пол студента: %s.

            Адресат: %s.
            Ситуация: %s.
            Чего хочет добиться студент: %s.

            Параметры письма:
            - Стиль: %s
            - Срочность: %s
            - Тон: %s
            - Размер: %s
            - Самоирония: %s
            - Дополнительно: %s

            Требования:
            - Пиши от первого лица студента.
            - Используй грамматические формы, соответствующие указанному полу.
            - **НЕ ПРИДУМЫВАЙ** дополнительных фактов, которых нет в описании ситуации: не указывай название предмета, номер группы (кроме указанного), конкретные даты, имена других людей.
            - Если в описании ситуации не сказано, что студент не сдал конкретный предмет, не упоминай название предмета.
            - Не добавляй отчество, не меняй имя и фамилию.
            - Учти срочность: если срочность высокая или критическая, начни с извинения за срочность.
            - Соблюдай выбранный размер текста.
            - Если самоирония разрешена, можешь добавить лёгкий юмор.
            - Не добавляй в начало лишних символов
            - Всегда обращайся к преподавателю на вы, если в дополнительной информации не сказано обратного
            """,
            firstName, lastName, group,
            "MALE".equals(gender) ? "мужской" : ("FEMALE".equals(gender) ? "женский" : "другой"),
            recipient,
            params.getEventDescription(),
            params.getDesiredAction(),
            formalityRu, urgencyRu, toneRu, lengthRu,
            params.isSelfIronyAllowed() ? "да" : "нет",
            params.getCustomDetails() != null ? params.getCustomDetails() : "нет",
            firstName, lastName
        );
    }
    
    private String getFallbackResponse() {
        return """
            <div style="background: #fef3c7; padding: 20px; border-radius: 10px; border-left: 4px solid #f59e0b;">
                <p><strong>⚠️ Временные технические сложности</strong></p>
                <p>Сервис генерации временно недоступен. Вот шаблон, который вы можете использовать:</p>
                <hr>
                <p>Уважаемый преподаватель!</p>
                <p>Пишу вам, чтобы объяснить ситуацию с несданной работой. К сожалению, я не смог(ла) сдать 
                задание вовремя. Приношу свои извинения и обязуюсь сдать работу в ближайшее время.</p>
                <p><em>Сгенерировано автономным режимом</em></p>
            </div>
            """;
    }
}
