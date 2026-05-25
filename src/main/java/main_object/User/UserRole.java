/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.User;

/**
 *
 * @author Ирина
 */
public enum UserRole {
    STUDENT("student"),
    ADMIN("admin");
    
    private final String roleName;
    
    UserRole(String roleName) {
        this.roleName = roleName;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public static UserRole fromString(String roleName) {
        if (roleName == null) {
            return STUDENT;
        }        
        for (UserRole role : values()) {
            if (role.roleName.equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return STUDENT;
    }
}
