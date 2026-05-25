/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.SQLException;
import java.util.Optional;
import main_object.User.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import main_object.User.UserFactory;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Ирина
 */
public class UserRepository {
    
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM excuse_generator.users WHERE email = ? AND is_active = true";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email.toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UserFactory.createFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    public User register(String email, String rawPassword) throws SQLException {
        if (findByEmail(email).isPresent()) {
            throw new SQLException("User already exists");
        }
        
        String passwordHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
        
        String sql = "INSERT INTO excuse_generator.users (email, password_hash, role) VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email.toLowerCase());
            stmt.setString(2, passwordHash);
            stmt.setString(3, "student");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return UserFactory.createNewUser(email, passwordHash);
                }
            }
        }
        throw new SQLException("Ошибка регистрации");
    }
    
    public Optional<User> authenticate(String email, String rawPassword) throws SQLException {
    String sql = "SELECT * FROM excuse_generator.users WHERE email = ? AND is_active = true";

    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email.toLowerCase());

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String passwordHash = rs.getString("password_hash");
                if (BCrypt.checkpw(rawPassword, passwordHash)) {
                    User user = UserFactory.createFromResultSet(rs);
                    return Optional.of(user);
                }
            }
        }
    }
    return Optional.empty();
    }
}

