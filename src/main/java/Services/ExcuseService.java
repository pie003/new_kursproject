/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

/**
 *
 * @author Ирина
 */
import db.ExcuseParamsRepository;
import db.GenerationRequestRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import main_object.Excuse.ExcuseParams;
import main_object.Request.GenerationRequest;
import main_object.Request.GenerationRequestFactory;
import main_object.Request.RequestStatus;
import org.springframework.stereotype.Service;

@Service
public class ExcuseService {
    private final ExcuseParamsRepository paramsRepository = new ExcuseParamsRepository();
    private final GenerationRequestRepository requestRepository = new GenerationRequestRepository();
    private final GigaChatService gigaChatService = new GigaChatService();
    
    // Создание нового запроса
    public GenerationRequest createRequest(Long userId, ExcuseParams params) throws SQLException {
        ExcuseParams savedParams = paramsRepository.save(params);
        params.setId(savedParams.getId());
        
        GenerationRequest request = GenerationRequestFactory.createNewRequest(userId, params);
        return requestRepository.save(request);
    }

    // Сохранение сгенерированного текста
    public GenerationRequest saveGeneratedText(Long requestId, String generatedText) throws SQLException {
        Optional<GenerationRequest> requestOpt = requestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Запрос не найден");
        }
        GenerationRequest request = requestOpt.get();
        request.setGeneratedText(generatedText);
        request.setStatus(RequestStatus.SUCCESS);
        return requestRepository.update(request);
    }
    
    // Отметить как сохранённый (в избранное)
    public GenerationRequest markAsSaved(Long requestId) throws SQLException {
        Optional<GenerationRequest> requestOpt = requestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Запрос не найден");
        }
        GenerationRequest request = requestOpt.get();
        request.setSaved(true);
        return requestRepository.update(request);
    }
    
    // История всех запросов пользователя
    public List<GenerationRequest> getUserHistory(Long userId) throws SQLException {
        return requestRepository.findByUserId(userId);
    }
    
    // Сохранённые запросы пользователя
    public List<GenerationRequest> getUserSavedRequests(Long userId) throws SQLException {
        return requestRepository.findSavedByUserId(userId);
    }
    
    // Получить запрос по ID
    public Optional<GenerationRequest> getRequestById(Long id) throws SQLException {
        return requestRepository.findById(id);
    }
    
    public String generateText(ExcuseParams params) {
        String prompt = gigaChatService.buildPrompt(
            params.getEventType().getDisplayName(),
            params.getRecipient(),
            params.getFormalityLevel().getDisplayName(),
            params.getUrgency().getDisplayName(),
            params.getTone().getDisplayName(),
            params.isSelfIronyAllowed(),
            params.getCustomDetails()
        );
        
        return gigaChatService.sendPrompt(prompt);
    }
}
