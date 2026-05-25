/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Excuse;

/**
 *
 * @author Ирина
 */
public enum Urgency {
    LOW("LOW", "Низкая", 1, "Можно ответить в течение нескольких дней"),
    MEDIUM("MEDIUM", "Средняя", 2, "Желательно ответить в течение дня"),
    HIGH("HIGH", "Высокая", 3, "Требуется срочный ответ"),
    CRITICAL("CRITICAL", "Критическая", 4, "Ответ нужен немедленно");
    
    private final String code;
    private final String displayName;
    private final int level;
    private final String description;
    
    Urgency(String code, String displayName, int level, String description) {
        this.code = code;
        this.displayName = displayName;
        this.level = level;
        this.description = description;
    }
    
    public String getCode() { 
        return code; 
    }
    
    public String getDisplayName() { 
        return displayName; 
    }
    public int getLevel() { 
        return level; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public static Urgency fromCode(String code) {
        if (code == null) {
            return MEDIUM;
        }
        
        for (Urgency urgency : values()) {
            if (urgency.code.equalsIgnoreCase(code)) {
                return urgency;
            }
        }
        return MEDIUM;
    }
}
