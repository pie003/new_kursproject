/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Excuse;

/**
 *
 * @author Ирина
 */
public enum EventType {
    MISSED_DEADLINE("MISSED_DEADLINE", "Пропуск дедлайна", "Я не успел сдать работу вовремя"),
    TECHNICAL_ISSUE("TECHNICAL_ISSUE", "Технические проблемы", "Сломался компьютер/интернет"),
    HEALTH_ISSUES("HEALTH_ISSUES", "Проблемы со здоровьем", "Плохое самочувствие"),
    FAMILY_REASONS("FAMILY_REASONS", "Семейные обстоятельства", "Неотложные семейные дела"),
    WORK_OVERLOAD("WORK_OVERLOAD", "Перегрузка на работе", "Слишком много задач одновременно"),
    MISUNDERSTANDING("MISUNDERSTANDING", "Неправильное понимание задания", "Я неправильно понял требования"),
    LOST_FILES("LOST_FILES", "Потеря файлов", "Файлы случайно удалились или повредились"),
    DOG_REASON("DOG_REASON", "Работа повреждена", "Мою работу съела собака"),
    OTHER("OTHER", "Другое", "Причина не подходит под остальные категории");
    
    private final String code;
    private final String displayName;
    private final String description;
    
    EventType(String code, String displayName, String description) {
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
    
     public static EventType fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return OTHER;
        }
        
        for (EventType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return OTHER;
    }
}
