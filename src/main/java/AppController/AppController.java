/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AppController;

import Services.ExcuseService;
import Services.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import main_object.Excuse.ExcuseFactory;
import main_object.Excuse.ExcuseParams;
import main_object.Request.GenerationRequest;
import main_object.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Ирина
 */

@Controller
public class AppController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private ExcuseService excuseService;
    
    /**
     * Главная страница
     */
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        if (session.getAttribute("userId") != null) {
            model.addAttribute("userEmail", session.getAttribute("userEmail"));
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        try {
            var userOpt = userService.login(email, password);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                session.setAttribute("userId", user.getId());
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute("userRole", user.getRole().getRoleName());
                return "redirect:/dashboard";
            }
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка базы данных: " + e.getMessage());
            return "login";
        }
        model.addAttribute("error", "Неверный email или пароль");
        return "login";
    }
    
    /**
     * Страница регистрации
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    /**
     * Обработка формы регистрации
     */
    @PostMapping("/register")
    public String doRegister(@RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "register";
        }
        
        try {
            userService.register(email, password);
            model.addAttribute("success", "Регистрация прошла успешно! Теперь вы можете войти.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                model.addAttribute("error", "Пользователь с таким email уже существует");
            } else {
                model.addAttribute("error", "Ошибка базы данных: " + e.getMessage());
            }
            return "register";
        }
    }
    
    /**
     * Личный кабинет
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            int totalRequests = excuseService.getUserHistory(userId).size();
            int savedCount = excuseService.getUserSavedRequests(userId).size();
            
            model.addAttribute("userEmail", session.getAttribute("userEmail"));
            model.addAttribute("totalRequests", totalRequests);
            model.addAttribute("savedCount", savedCount);
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка загрузки данных: " + e.getMessage());
        }
        return "dashboard";
    }
    
    /**
     * Страница создания нового объяснения
     */
    @GetMapping("/new-excuse")
    public String newExcuse(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        return "new-excuse";
    }
    
    /**
     * Генерация объяснения
     */
    @PostMapping("/generate")
    public String generate(@RequestParam String eventType,
                           @RequestParam String recipient,
                           @RequestParam String formalityLevel,
                           @RequestParam String urgency,
                           @RequestParam String tone,
                           @RequestParam(required = false) String selfIrony,
                           @RequestParam(required = false) String customDetails,
                           HttpSession session,
                           Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            // Создаём параметры через фабрику
            ExcuseParams params = ExcuseFactory.createFromForm(
                eventType, recipient, formalityLevel, urgency, tone,
                selfIrony != null, customDetails
            );
            
            // Создаём запрос и генерируем текст
            GenerationRequest request = excuseService.createRequest(userId, params);
            String generatedText = excuseService.generateText(params);
            excuseService.saveGeneratedText(request.getId(), generatedText);
            
            model.addAttribute("generatedText", generatedText);
            model.addAttribute("requestId", request.getId());
            model.addAttribute("params", params);
            
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка при генерации: " + e.getMessage());
        }
        return "new-excuse";
    }
    
    /**
     * История запросов
     */
    @GetMapping("/history")
    public String history(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            model.addAttribute("requests", excuseService.getUserHistory(userId));
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка загрузки истории: " + e.getMessage());
        }
        return "history";
    }
    
    /**
     * Сохранённые объяснения
     */
    @GetMapping("/saved")
    public String saved(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            model.addAttribute("requests", excuseService.getUserSavedRequests(userId));
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка загрузки сохранённых: " + e.getMessage());
        }
        return "saved";
    }
    
    /**
     * Сохранить запрос в избранное
     */
    @PostMapping("/save-request")
    public String saveRequest(@RequestParam Long requestId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            excuseService.markAsSaved(requestId);
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении запроса: " + e.getMessage());
        }
        return "redirect:/saved";
    }
    
    /**
     * Просмотр конкретного запроса
     */
    @GetMapping("/view-request/{id}")
    public String viewRequest(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            var requestOpt = excuseService.getRequestById(id);
            if (requestOpt.isPresent() && requestOpt.get().getUserId().equals(userId)) {
                model.addAttribute("request", requestOpt.get());
                return "view-request";
            } else {
                return "redirect:/history";
            }
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка загрузки: " + e.getMessage());
            return "history";
        }
    }
    
    /**
     * Выход из аккаунта
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }  
    @PostMapping("/demo-generate")
    public String demoGenerate(@RequestParam String eventType,
                               @RequestParam String recipient,
                               @RequestParam(required = false, defaultValue = "NEUTRAL") String tone,
                               @RequestParam(required = false, defaultValue = "MEDIUM") String formality,
                               Model model) {

        String eventTypeRu = getEventTypeRussian(eventType);
        String toneRu = getToneRussian(tone);

        String demoText = String.format("""
            <div style="background: #fef3c7; padding: 15px; border-radius: 10px;">
                <p><strong>📨 %s</strong></p>
                <p>Пишу вам, чтобы объяснить ситуацию с несданной работой.</p>
                <p>К сожалению, я не смог(ла) сдать задание вовремя из-за <strong>%s</strong>.</p>
                <p>Я выбрал(а) <strong>%s</strong> тон для этого обращения.</p>
                <p>Приношу свои извинения и обязуюсь сдать работу в ближайшее время.</p>
                <hr>
                <p><em>✨ Это демо-версия. <a href="/register" style="color: #d97706;">Зарегистрируйтесь</a>, чтобы сохранять результаты!</em></p>
            </div>
            """, recipient, eventTypeRu, toneRu);

        model.addAttribute("demoText", demoText);
        return "index";
    }

    private String getEventTypeRussian(String eventType) {
        switch (eventType) {
            case "MISSED_DEADLINE": return "пропуске дедлайна";
            case "TECHNICAL_ISSUE": return "технических проблемах";
            case "HEALTH_ISSUES": return "проблемах со здоровьем";
            case "FAMILY_REASONS": return "семейных обстоятельствах";
            default: return "сложившейся ситуации";
        }
    }

    private String getToneRussian(String tone) {
        switch (tone) {
            case "NEUTRAL": return "нейтральный";
            case "APOLOGETIC": return "извиняющийся";
            case "CONSTRUCTIVE": return "конструктивный";
            default: return "нейтральный";
        }
    }
}
