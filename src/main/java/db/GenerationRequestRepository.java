/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main_object.Excuse.ExcuseParams;
import main_object.Request.GenerationRequest;
import main_object.Request.GenerationRequestFactory;

/**
 *
 * @author Ирина
 */
public class GenerationRequestRepository {
    public GenerationRequest save(GenerationRequest request) throws SQLException {
        if (request.getId() == null) return insert(request);
        else return update(request);
    }
    
    private GenerationRequest insert(GenerationRequest request) throws SQLException {
        String sql = """
            INSERT INTO excuse_generator.requests 
            (user_id, params_id, generated_text, status, created_at, updated_at, is_saved, error_message) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?) 
            RETURNING id
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, request.getUserId());
            stmt.setLong(2, request.getParams().getId());
            stmt.setString(3, request.getGeneratedText());
            stmt.setString(4, request.getStatus().getCode());
            stmt.setTimestamp(5, Timestamp.valueOf(request.getCreatedAt()));
            stmt.setTimestamp(6, Timestamp.valueOf(request.getUpdatedAt()));
            stmt.setBoolean(7, request.isSaved());
            stmt.setString(8, request.getErrorMessage());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) request.setId(rs.getLong("id"));
            }
        }
        return request;
    }
    
    public GenerationRequest update(GenerationRequest request) throws SQLException {
        String sql = """
            UPDATE excuse_generator.requests
            SET params_id = ?, generated_text = ?, status = ?, updated_at = ?, is_saved = ?, error_message = ?
            WHERE id = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, request.getParams().getId());
            stmt.setString(2, request.getGeneratedText());
            stmt.setString(3, request.getStatus().getCode());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(5, request.isSaved());
            stmt.setString(6, request.getErrorMessage());
            stmt.setLong(7, request.getId());
            stmt.executeUpdate();
        }
        return request;
    }
    
    public GenerationRequest saveOrUpdate(GenerationRequest request) throws SQLException {
        if (request.getId() == null) {
            return save(request);
        } else {
            return update(request);
        }
    }
    
    public Optional<GenerationRequest> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM excuse_generator.requests WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    GenerationRequest request = GenerationRequestFactory.createFromResultSet(rs);
                    Long paramsId = rs.getLong("params_id");
                    if (paramsId > 0) {
                        request.setParams(ExcuseParamsRepository.findById(paramsId).orElse(null));
                    }
                    return Optional.of(request);
                }
            }
        }
        return Optional.empty();
    }
    
    public List<GenerationRequest> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM excuse_generator.requests WHERE user_id = ? ORDER BY created_at DESC";
        List<GenerationRequest> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GenerationRequest request = GenerationRequestFactory.createFromResultSet(rs);
                    Long paramsId = rs.getLong("params_id");
                    if (paramsId > 0) {
                        request.setParams(ExcuseParamsRepository.findById(paramsId).orElse(null));
                    }
                    requests.add(request);
                }
            }
        }
        return requests;
    }
    
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM excuse_generator.requests WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    public List<GenerationRequest> findSavedByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM excuse_generator.requests WHERE user_id = ? AND is_saved = true ORDER BY created_at DESC";
        List<GenerationRequest> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GenerationRequest request = GenerationRequestFactory.createFromResultSet(rs);
                    Long paramsId = rs.getLong("params_id");
                    if (paramsId > 0) {
                        request.setParams(ExcuseParamsRepository.findById(paramsId).orElse(null));
                    }
                    requests.add(request);
                }
            }
        }
        return requests;
    }
    
    public Optional<GenerationRequest> findCurrentDraft(Long userId) throws SQLException {
        String sql = "SELECT * FROM excuse_generator.requests WHERE user_id = ? AND status IN ('draft') ORDER BY updated_at DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(GenerationRequestFactory.createFromResultSet(rs));
            }
        }
        return Optional.empty();
    }
    
    public List<GenerationRequest> findSavedAndCompleted(Long userId, String search, String recipient,
                                                            String formalityLevel, String urgency,
                                                            String tone, String length) throws SQLException {
        StringBuilder sql = new StringBuilder("""
        SELECT gr.* FROM excuse_generator.requests gr
        JOIN excuse_generator.excuse_params ep ON gr.params_id = ep.id
        WHERE gr.user_id = ? AND is_saved = true
        """);
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (recipient != null && !recipient.trim().isEmpty()) {
            sql.append(" AND ep.recipient ILIKE ?");
            params.add("%" + recipient + "%");
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (gr.generated_text ILIKE ? OR ep.event_description ILIKE ?)");
            String like = "%" + search + "%";
            params.add(like);
            params.add(like);
        }
        if (formalityLevel != null && !formalityLevel.isEmpty()) {
            sql.append(" AND ep.formality_level = ?");
            params.add(formalityLevel);
        }
        if (urgency != null && !urgency.isEmpty()) {
            sql.append(" AND ep.urgency = ?");
            params.add(urgency);
        }
        if (tone != null && !tone.isEmpty()) {
            sql.append(" AND ep.tone = ?");
            params.add(tone);
        }
        if (length != null && !length.isEmpty()) {
            sql.append(" AND ep.length = ?");
            params.add(length);
        }
        sql.append(" ORDER BY gr.updated_at DESC");

        try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
        List<GenerationRequest> list = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                GenerationRequest request = GenerationRequestFactory.createFromResultSet(rs);
                Long paramsId = rs.getLong("params_id");
                if (paramsId > 0) {
                    request.setParams(ExcuseParamsRepository.findById(paramsId).orElse(null));
                }
                list.add(request);
            }
        }
        return list;
        }
    }
    
    public List<GenerationRequest> findDrafts(Long userId, String search, String recipient,
                                            String formalityLevel, String urgency,
                                            String tone, String length) throws SQLException {
     StringBuilder sql = new StringBuilder("""
         SELECT gr.*, ep.recipient, ep.event_description, ep.formality_level, ep.urgency, ep.tone, ep.length,
                ep.self_irony_allowed, ep.custom_details
         FROM excuse_generator.requests gr
         JOIN excuse_generator.excuse_params ep ON gr.params_id = ep.id
         WHERE gr.user_id = ? AND gr.status IN ('draft', 'failed')
     """);
     List<Object> params = new ArrayList<>();
     params.add(userId);

     if (recipient != null && !recipient.trim().isEmpty()) {
         sql.append(" AND ep.recipient ILIKE ?");
         params.add("%" + recipient + "%");
     }
     if (search != null && !search.trim().isEmpty()) {
         sql.append(" AND (gr.generated_text ILIKE ? OR ep.event_description ILIKE ?)");
         String like = "%" + search + "%";
         params.add(like);
         params.add(like);
     }
     if (formalityLevel != null && !formalityLevel.trim().isEmpty()) {
         sql.append(" AND ep.formality_level = ?");
         params.add(formalityLevel);
     }
     if (urgency != null && !urgency.trim().isEmpty()) {
         sql.append(" AND ep.urgency = ?");
         params.add(urgency);
     }
     if (tone != null && !tone.trim().isEmpty()) {
         sql.append(" AND ep.tone = ?");
         params.add(tone);
     }
     if (length != null && !length.trim().isEmpty()) {
         sql.append(" AND ep.length = ?");
         params.add(length);
     }
     sql.append(" ORDER BY gr.updated_at DESC");

     try (Connection conn = DatabaseConfig.getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
         for (int i = 0; i < params.size(); i++) {
             stmt.setObject(i + 1, params.get(i));
         }
         List<GenerationRequest> list = new ArrayList<>();
         try (ResultSet rs = stmt.executeQuery()) {
             while (rs.next()) {
                GenerationRequest request = GenerationRequestFactory.createFromResultSet(rs);
                Long paramsId = rs.getLong("params_id");
                if (paramsId > 0) {
                    request.setParams(ExcuseParamsRepository.findById(paramsId).orElse(null));
                }
                list.add(request);
             }
         }
         return list;
     }
 }
}
