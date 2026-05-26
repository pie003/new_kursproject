/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AppController;

import Services.ExcuseService;
import Services.GigaChatService;
import Services.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import main_object.Excuse.EventType;
import main_object.Excuse.ExcuseFactory;
import main_object.Excuse.ExcuseParams;
import main_object.Excuse.FormalityLevel;
import main_object.Excuse.Length;
import main_object.Excuse.Tone;
import main_object.Excuse.Urgency;
import main_object.Request.GenerationRequest;
import main_object.User.User;
import new_project.mew_project.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    
    @Autowired
    private GigaChatService gigaChatService;
    
    /**
     * Главная страница
     */
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        if (session.getAttribute("userId") != null) {
            model.addAttribute("userEmail", session.getAttribute("userEmail"));
        }
        // Передаём списки enum для выпадающих списков
        model.addAttribute("eventTypes", EventType.values());
        model.addAttribute("formalityLevels", FormalityLevel.values());
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
                return "redirect:/dashboard";
            }
        } catch (SQLException e) {
            LogManager.logError("Login failed for email: " + email, e);
            model.addAttribute("error", "Ошибка сервера. Пожалуйста, попробуйте позже.");
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
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String group,
                             @RequestParam String gender,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "register";
        }
        try {
            userService.register(email, password, firstName, lastName, group, gender);
            model.addAttribute("success", "Регистрация прошла успешно!");
            return "login";
        } catch (IllegalArgumentException | SQLException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    
    /**
     * Личный кабинет
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            // Загружаем пользователя из БД
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Добавляем в модель персональные данные
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("userFirstName", user.getFirstName());
            model.addAttribute("userLastName", user.getLastName());
            model.addAttribute("userGroup", user.getGroup());
            model.addAttribute("userGender", user.getGender());

            // Форматируем дату последнего входа без таймзоны
            String lastLoginFormatted = "";
            if (user.getLastLoginAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                lastLoginFormatted = user.getLastLoginAt().format(formatter);
            } else {
                lastLoginFormatted = "Первый вход";
            }
            model.addAttribute("lastLogin", lastLoginFormatted);

            // Статистика
            int totalRequests = excuseService.getUserHistory(userId).size();
            int savedCount = excuseService.getUserSavedRequests(userId).size();
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
    public String newExcuse(@RequestParam(required = false) Long requestId,
                            @RequestParam(required = false) Boolean clear,
                            HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        model.addAttribute("formalityLevels", FormalityLevel.values());
        model.addAttribute("urgencies", Urgency.values());
        model.addAttribute("tones", Tone.values());
        model.addAttribute("lengths", Length.values());
        
         if (requestId != null) {
            try {
                Optional<GenerationRequest> reqOpt = excuseService.getRequestById(requestId);
                if (reqOpt.isPresent() && reqOpt.get().getUserId().equals(userId)) {
                    GenerationRequest request = reqOpt.get();
                    model.addAttribute("requestId", request.getId());
                    model.addAttribute("params", request.getParams());  // ← ключевой момент
                    model.addAttribute("generatedText", request.getGeneratedText());
                    model.addAttribute("currentStatus", request.getStatus().getDisplayName());
                } else {
                    model.addAttribute("error", "Черновик не найден");
                }
            } catch (SQLException e) {
                model.addAttribute("error", "Ошибка загрузки черновика");
            }
        }
         
        return "new-excuse";
    }

    @PostMapping("/generate")
    public String generate(@RequestParam(required = false) Long requestId,
                           @RequestParam String eventDescription,
                           @RequestParam String desiredAction,
                           @RequestParam String recipient,
                           @RequestParam String formalityLevel,
                           @RequestParam String urgency,
                           @RequestParam String tone,
                           @RequestParam(required = false) String selfIrony,
                           @RequestParam(required = false) String customDetails,
                           @RequestParam String length,
                           HttpSession session,
                           Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        try {
            ExcuseParams params = ExcuseFactory.createFromForm(
                eventDescription, desiredAction, recipient, formalityLevel, urgency, tone,
                selfIrony != null, customDetails, length
            );
            GenerationRequest request = excuseService.createOrUpdateRequest(userId, params, requestId);
            User user = userService.getUserById(userId).orElseThrow();
            String generatedText = excuseService.generateText(params, user);
            excuseService.saveGeneratedText(request.getId(), generatedText);
            model.addAttribute("generatedText", generatedText);
            model.addAttribute("requestId", request.getId());
            model.addAttribute("params", params);
            model.addAttribute("formalityLevels", FormalityLevel.values());
            model.addAttribute("urgencies", Urgency.values());
            model.addAttribute("tones", Tone.values());
            model.addAttribute("lengths", Length.values());
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка генерации: " + e.getMessage());
        }
        return "new-excuse";
    }

    @PostMapping("/save-request")
    public String saveRequest(@RequestParam Long requestId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        try {
            excuseService.markAsSaved(requestId);
            redirectAttributes.addFlashAttribute("success", "Запрос добавлен в избранное");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка сохранения");
        }
        return "redirect:/new-excuse";
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
    public String saved(@RequestParam(required = false) String search,
                        @RequestParam(required = false) String recipient,
                        @RequestParam(required = false) String formalityLevel,
                        @RequestParam(required = false) String urgency,
                        @RequestParam(required = false) String tone,
                        @RequestParam(required = false) String length,
                        HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        try {
            List<GenerationRequest> requests;
            // Если есть хоть один непустой фильтр – применяем фильтрацию
            if (hasAnyFilter(search, recipient, formalityLevel, urgency, tone, length)) {
                requests = excuseService.getSavedAndCompletedRequests(userId, search, recipient, formalityLevel, urgency, tone, length);
                model.addAttribute("search", search);
                model.addAttribute("recipient", recipient);
                model.addAttribute("selectedFormality", formalityLevel);
                model.addAttribute("selectedUrgency", urgency);
                model.addAttribute("selectedTone", tone);
                model.addAttribute("selectedLength", length);
            } else {
                requests = excuseService.getUserSavedRequests(userId);
                // Не передаём фильтры – поля останутся пустыми
            }
            model.addAttribute("requests", requests);
            model.addAttribute("formalityLevels", FormalityLevel.values());
            model.addAttribute("urgencies", Urgency.values());
            model.addAttribute("tones", Tone.values());
            model.addAttribute("lengths", Length.values());
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка загрузки сохранённых: " + e.getMessage());
        }
        return "saved";
    }

    private boolean hasAnyFilter(String... values) {
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return true;
        }
        return false;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    @PostMapping("/demo-generate")
    public String demoGenerate(@RequestParam String eventType,
                           @RequestParam String recipient,
                           @RequestParam String formalityLevel,
                           Model model) {
    String generatedText = gigaChatService.generateDemo(eventType, recipient, formalityLevel);
    model.addAttribute("demoText", generatedText);
    
    model.addAttribute("eventTypes", EventType.values());
    model.addAttribute("formalityLevels", FormalityLevel.values());
        
    return "index";
}
    
    @PostMapping("/complete-request")
    public String completeRequest(@RequestParam Long requestId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        try {
            excuseService.markAsCompleted(requestId);
            redirectAttributes.addFlashAttribute("success", "Запрос сохранён");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка завершения");
        }
        return "new-excuse";
    }

    @GetMapping("/request/{id}")
    public String viewRequest(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        try {
            Optional<GenerationRequest> opt = excuseService.getRequestById(id);
            if (opt.isPresent() && opt.get().getUserId().equals(userId)) {
                GenerationRequest request = opt.get();
                model.addAttribute("request", request);
                model.addAttribute("formalityDisplay", request.getParams().getFormalityLevel().getDisplayName());
                model.addAttribute("urgencyDisplay", request.getParams().getUrgency().getDisplayName());
                model.addAttribute("toneDisplay", request.getParams().getTone().getDisplayName());
                model.addAttribute("lengthDisplay", request.getParams().getLength().getDisplayName());
                return "request-details";
            } 
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка загрузки деталей");
        }
        return "redirect:/saved";
    }
    
    @GetMapping("/drafts")
    public String drafts(@RequestParam(required = false) String search,
                         @RequestParam(required = false) String recipient,
                         @RequestParam(required = false) String formalityLevel,
                         @RequestParam(required = false) String urgency,
                         @RequestParam(required = false) String tone,
                         @RequestParam(required = false) String length,
                         HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        try {
            List<GenerationRequest> drafts = excuseService.getUserDrafts(userId, search, recipient,
                    formalityLevel, urgency, tone, length);
            model.addAttribute("requests", drafts);
            model.addAttribute("search", search != null ? search : "");
            model.addAttribute("recipient", recipient != null ? recipient : "");
            model.addAttribute("selectedFormality", formalityLevel != null ? formalityLevel : "");
            model.addAttribute("selectedUrgency", urgency != null ? urgency : "");
            model.addAttribute("selectedTone", tone != null ? tone : "");
            model.addAttribute("selectedLength", length != null ? length : "");
            model.addAttribute("formalityLevels", FormalityLevel.values());
            model.addAttribute("urgencies", Urgency.values());
            model.addAttribute("tones", Tone.values());
            model.addAttribute("lengths", Length.values());
        } catch (SQLException e) {
            model.addAttribute("error", "Ошибка загрузки черновиков");
        }
        return "drafts";
    }

    @PostMapping("/delete-draft")
    public String deleteDraft(@RequestParam Long requestId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        try {
            // Доп. проверка, что черновик принадлежит пользователю
            Optional<GenerationRequest> draft = excuseService.getRequestById(requestId);
            if (draft.isPresent() && draft.get().getUserId().equals(userId)) {
                excuseService.deleteDraft(requestId);
            }
        } catch (SQLException e) {
            // лог
        }
        return "redirect:/drafts";
    }
    
    @GetMapping("/edit-profile")
public String editProfile(HttpSession session, Model model) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) return "redirect:/login";
    try {
        User user = userService.getUserById(userId).orElseThrow();
        model.addAttribute("user", user);
    } catch (SQLException e) {
        model.addAttribute("error", "Ошибка загрузки профиля");
    }
    return "edit-profile";
}

    @PostMapping("/edit-profile")
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String group,
                                @RequestParam String gender,
                                @RequestParam(required = false) String password,
                                @RequestParam(required = false) String confirmPassword,
                                HttpSession session, RedirectAttributes redirectAttributes) throws SQLException {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        // Если пароль передан и не пуст, проверяем совпадение
        if (password != null && !password.trim().isEmpty()) {
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Пароли не совпадают");
                return "redirect:/edit-profile";
            }
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Пароль должен быть не менее 6 символов");
                return "redirect:/edit-profile";
            }
            // Обновляем пароль
            userService.changePassword(userId, password);
        }
        // Обновляем остальные поля
        userService.updateProfile(userId, firstName, lastName, group, gender);
        redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлён");
        // Обновляем email в сессии? Не меняем email, он остаётся прежним.
        return "redirect:/dashboard";
    }
}
