/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

import db.UserRepository;
import java.sql.SQLException;
import java.util.Optional;
import main_object.User.User;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ирина
 */
@Service
public class UserService {
    private final UserRepository userRepository = new UserRepository();
    
    public User register(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Пароль должен быть не менее 6 символов");
        }
        return userRepository.register(email, password);
    }
    
    public Optional<User> login(String email, String password) throws SQLException {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return userRepository.authenticate(email, password);
    }
}
