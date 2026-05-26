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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import main_object.Excuse.ExcuseParams;
import main_object.Request.GenerationRequest;
import main_object.Request.RequestStatus;
import main_object.User.User;
import org.springframework.stereotype.Service;

@Service
public class ExcuseService {
    private final ExcuseParamsRepository paramsRepository = new ExcuseParamsRepository();
    private final GenerationRequestRepository requestRepository = new GenerationRequestRepository();
    private final GigaChatService gigaChatService = new GigaChatService();
    
    // Создание нового запроса
    public GenerationRequest createOrUpdateRequest(Long userId, ExcuseParams params, Long existingRequestId) throws SQLException {
        if (existingRequestId != null) {
            Optional<GenerationRequest> existing = requestRepository.findById(existingRequestId);
            if (existing.isPresent() && existing.get().getUserId().equals(userId)) {
                GenerationRequest req = existing.get();
                // Обновляем параметры
                ExcuseParams savedParams = paramsRepository.save(params); // сохраняем (если новые) или можно обновить
                req.setParams(savedParams);
                req.setUpdatedAt(LocalDateTime.now());
                req.setStatus(RequestStatus.DRAFT);
                return requestRepository.update(req);
            }
        }
        // Создаём новый
        ExcuseParams savedParams = paramsRepository.save(params);
        GenerationRequest newReq = new GenerationRequest();
        newReq.setUserId(userId);
        newReq.setParams(savedParams);
        newReq.setStatus(RequestStatus.DRAFT);
        newReq.setCreatedAt(LocalDateTime.now());
        newReq.setUpdatedAt(LocalDateTime.now());
        newReq.setSaved(false);
        return requestRepository.save(newReq);
    }
    
    public Optional<GenerationRequest> getCurrentDraft(Long userId) throws SQLException {
        return requestRepository.findCurrentDraft(userId);
    }
    
    public void saveGeneratedText(Long requestId, String text) throws SQLException {
        Optional<GenerationRequest> opt = requestRepository.findById(requestId);
        if (opt.isPresent()) {
            GenerationRequest req = opt.get();
            req.setGeneratedText(text);
            req.setUpdatedAt(LocalDateTime.now());
            req.setStatus(RequestStatus.DRAFT);
            requestRepository.update(req);
        }
    }
    
    public void markAsSaved(Long requestId) throws SQLException {
        Optional<GenerationRequest> opt = requestRepository.findById(requestId);
        if (opt.isPresent()) {
            GenerationRequest req = opt.get();
            req.setStatus(RequestStatus.SAVED);
            req.setSaved(true);
            requestRepository.update(req);
        }
    }
    
    public String generateText(ExcuseParams params, User user) {
        String prompt = gigaChatService.buildPrompt(params, user);
        return gigaChatService.sendPrompt(prompt);
    }
    
    public List<GenerationRequest> getUserHistory(Long userId) throws SQLException {
        return requestRepository.findByUserId(userId);
    }
    
    public List<GenerationRequest> getUserSavedRequests(Long userId) throws SQLException {
        return requestRepository.findSavedByUserId(userId);
    }
    
    public void markAsCompleted(Long requestId) throws SQLException {
        Optional<GenerationRequest> opt = requestRepository.findById(requestId);
        if (opt.isPresent()) {
            GenerationRequest req = opt.get();
            req.setStatus(RequestStatus.COMPLETED);
            req.setSaved(true);
            requestRepository.update(req);
        }
    }
    
    public List<GenerationRequest> getSavedAndCompletedRequests(Long userId, String search, String recipient,
                                                             String formalityLevel, String urgency,
                                                             String tone, String length) throws SQLException {
        return requestRepository.findSavedAndCompleted(userId, search, recipient, formalityLevel, urgency, tone, length);
    }
    
    public Optional<GenerationRequest> getRequestById(Long id) throws SQLException {
        return requestRepository.findById(id);
    }
    
    public List<GenerationRequest> getUserDrafts(Long userId, String search, String recipient,
                                              String formalityLevel, String urgency,
                                              String tone, String length) throws SQLException {
    return requestRepository.findDrafts(userId, search, recipient, formalityLevel, urgency, tone, length);
}

    public void deleteDraft(Long requestId) throws SQLException {
        requestRepository.deleteById(requestId);
    }
}
