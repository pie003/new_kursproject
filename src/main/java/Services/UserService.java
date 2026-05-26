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
    
    public User register(String email, String password, String first_name, String last_name, String group, String gender) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Пароль должен быть не менее 6 символов");
        }
        if (first_name == null || first_name.trim().isEmpty()) throw new IllegalArgumentException("Имя обязательно");
        if (last_name == null || last_name.trim().isEmpty()) throw new IllegalArgumentException("Фамилия обязательна");
        if (group == null || group.trim().isEmpty()) throw new IllegalArgumentException("Группа обязательна");
        if (gender == null || (!gender.equals("MALE") && !gender.equals("FEMALE") && !gender.equals("OTHER"))) {
            throw new IllegalArgumentException("Некорректное значение пола");
        }
        return userRepository.register(email, password, first_name, last_name, group, gender);
    }
    
    public Optional<User> login(String email, String password) throws SQLException {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return userRepository.authenticate(email, password);
    }
    
    public Optional<User> getUserById(Long id) throws SQLException {
        return userRepository.findById(id);
    }
    
    public void changePassword(Long userId, String newPassword) throws SQLException {
        userRepository.changePassword(userId, newPassword);
    }

    public void updateProfile(Long userId, String firstName, String lastName, String group, String gender) throws SQLException {
        userRepository.updateProfile(userId, firstName, lastName, group, gender);
    }
}
