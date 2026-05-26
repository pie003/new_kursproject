/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Request;

import java.time.LocalDateTime;
import main_object.Excuse.ExcuseParams;

/**
 *
 * @author Ирина
 */
public class GenerationRequestFactory {
    public static GenerationRequest createNewRequest(Long userId, ExcuseParams params) {
        GenerationRequest request = new GenerationRequest();
        request.setUserId(userId);
        request.setParams(params);
        request.setStatus(RequestStatus.DRAFT);
        request.setCreatedAt(LocalDateTime.now());
        request.setSaved(false);
        return request;
    }
    
    public static GenerationRequest createFromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        GenerationRequest request = new GenerationRequest();
        request.setId(rs.getLong("id"));
        request.setUserId(rs.getLong("user_id"));
        request.setGeneratedText(rs.getString("generated_text"));
        request.setStatus(RequestStatus.fromCode(rs.getString("status")));
        request.setSaved(rs.getBoolean("is_saved"));
        
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            request.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        request.setErrorMessage(rs.getString("error_message"));
        return request;
    }
}
