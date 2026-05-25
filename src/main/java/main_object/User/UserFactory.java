/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.User;

import java.time.LocalDateTime;

/**
 *
 * @author Ирина
 */
public class UserFactory {
    public static User createNewUser(String email, String passwordHash) {
        User user = new User();
        user.setEmail(email.toLowerCase());
        user.setPasswordHash(passwordHash);
        user.setRole(UserRole.STUDENT);
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        return user;
    }
    
    public static User createFromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.fromString(rs.getString("role")));
        
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        java.sql.Timestamp lastLoginAt = rs.getTimestamp("last_login_at");
        if (lastLoginAt != null) {
            user.setLastLoginAt(lastLoginAt.toLocalDateTime());
        }
        
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}
