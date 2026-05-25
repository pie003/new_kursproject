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
    
    // Создание нового запроса
    public GenerationRequest createRequest(Long userId, ExcuseParams params) throws SQLException {
        ExcuseParams savedParams = paramsRepository.save(params);
        params.setId(savedParams.getId());
        
        GenerationRequest request = GenerationRequestFactory.createNewRequest(userId, params);
        return requestRepository.save(request);
    }
    
    // Генерация текста (пока заглушка, потом заменим на GigaChat)
    public String generateText(ExcuseParams params) {
        return String.format("""
            <div style="background: #f0f4ff; padding: 20px; border-radius: 10px;">
                <p><strong>%s</strong></p>
                <p>Пишу вам, чтобы объяснить ситуацию с несданной работой.</p>
                <p>К сожалению, я не смог(ла) сдать задание вовремя из-за %s.</p>
                <p>Приношу извинения и обязуюсь сдать работу.</p>
            </div>
            """, params.getRecipient(), params.getEventType().getDisplayName());
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
}
