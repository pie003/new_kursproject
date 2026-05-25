/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Excuse;

/**
 *
 * @author Ирина
 */
public enum Tone {
    NEUTRAL("NEUTRAL", "Нейтральный", "Без эмоциональной окраски"),
    APOLOGETIC("APOLOGETIC", "Извиняющийся", "С акцентом на извинения"),
    CONSTRUCTIVE("CONSTRUCTIVE", "Конструктивный", "С фокусом на решение проблемы"),
    HUMOROUS("HUMOROUS", "С юмором", "Легкий, с долей юмора"),
    PROFESSIONAL("PROFESSIONAL", "Профессиональный", "Сухо, по делу, факты"),
    EMOTIONAL("EMOTIONAL", "Эмоциональный", "С выражением искренних чувств");
    
    private final String code;
    private final String displayName;
    private final String description;
    
    Tone(String code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getCode() { 
        return code; 
    }
    
    public String getDisplayName() { 
        return displayName; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public static Tone fromCode(String code) {
        if (code == null) {
            return NEUTRAL;
        }
        
        for (Tone tone : values()) {
            if (tone.code.equalsIgnoreCase(code)) {
                return tone;
            }
        }
        return NEUTRAL;
    }
}
