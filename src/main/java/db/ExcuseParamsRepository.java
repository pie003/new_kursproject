/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import main_object.Excuse.ExcuseFactory;
import main_object.Excuse.ExcuseParams;

/**
 *
 * @author Ирина
 */
public class ExcuseParamsRepository {
    public ExcuseParams save(ExcuseParams params) throws SQLException {
        String sql = """
            INSERT INTO excuse_generator.excuse_params 
            (event_type, recipient, formality_level, urgency, tone, self_irony_allowed, custom_details) 
            VALUES (?, ?, ?, ?, ?, ?, ?) 
            RETURNING id
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, params.getEventType().getCode());
            stmt.setString(2, params.getRecipient());
            stmt.setString(3, params.getFormalityLevel().getCode());
            stmt.setString(4, params.getUrgency().getCode());
            stmt.setString(5, params.getTone().getCode());
            stmt.setBoolean(6, params.isSelfIronyAllowed());
            stmt.setString(7, params.getCustomDetails());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    params.setId(rs.getLong("id"));
                }
            }
        }
        return params;
    }
    
    public ExcuseParams update(ExcuseParams params) throws SQLException {
        String sql = """
            UPDATE excuse_generator.excuse_params 
            SET event_type = ?, recipient = ?, formality_level = ?, 
                urgency = ?, tone = ?, self_irony_allowed = ?, custom_details = ?
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, params.getEventType().getCode());
            stmt.setString(2, params.getRecipient());
            stmt.setString(3, params.getFormalityLevel().getCode());
            stmt.setString(4, params.getUrgency().getCode());
            stmt.setString(5, params.getTone().getCode());
            stmt.setBoolean(6, params.isSelfIronyAllowed());
            stmt.setString(7, params.getCustomDetails());
            stmt.setLong(8, params.getId());
            
            stmt.executeUpdate();
        }
        return params;
    }
    
    public static Optional<ExcuseParams> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM excuse_generator.excuse_params WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(ExcuseFactory.createFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM excuse_generator.excuse_params WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}
