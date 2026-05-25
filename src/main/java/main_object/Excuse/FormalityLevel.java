/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Excuse;

/**
 *
 * @author Ирина
 */
public enum FormalityLevel {
    INFORMAL("INFORMAL", "Неформальный", 1, "Дружеский, простой язык"),
    MEDIUM("MEDIUM", "Средний", 2, "Обычный деловой стиль"),
    FORMAL("FORMAL", "Официальный", 3, "Строгий, официальный стиль"),
    VERY_FORMAL("VERY_FORMAL", "Максимально официальный", 4, "С использованием официальных оборотов");
    
    private final String code;
    private final String displayName;
    private final int level;
    private final String description;
    
    FormalityLevel(String code, String displayName, int level, String description) {
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
    
    public static FormalityLevel fromCode(String code) {
        if (code == null) {
            return MEDIUM;
        }
        
        for (FormalityLevel level : values()) {
            if (level.code.equalsIgnoreCase(code)) {
                return level;
            }
        }
        return MEDIUM;
    }
}
