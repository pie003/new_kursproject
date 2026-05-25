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
import main_object.Request.GenerationRequest;
import main_object.Request.GenerationRequestFactory;

/**
 *
 * @author Ирина
 */
public class GenerationRequestRepository {
    public GenerationRequest save(GenerationRequest request) throws SQLException {
        String sql = """
            INSERT INTO excuse_generator.requests 
            (user_id, params_id, generated_text, status, created_at, is_saved, error_message)  
            VALUES (?, ?, ?, ?, ?, ?, ?) 
            RETURNING id
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, request.getUserId());
            stmt.setLong(2, request.getParams().getId());
            stmt.setString(3, request.getGeneratedText());
            stmt.setString(4, request.getStatus().getCode());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(6, request.isSaved());
            stmt.setString(7, request.getErrorMessage());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    request.setId(rs.getLong("id"));
                }
            }
        }
        return request;
    }
    
    public GenerationRequest update(GenerationRequest request) throws SQLException {
        String sql = """
            UPDATE excuse_generator.requests 
            SET generated_text = ?, status = ?, is_saved = ?, error_message = ?
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, request.getGeneratedText());
            stmt.setString(2, request.getStatus().getCode());
            stmt.setBoolean(3, request.isSaved());
            stmt.setString(4, request.getErrorMessage());
            stmt.setLong(5, request.getId());
            
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
}
