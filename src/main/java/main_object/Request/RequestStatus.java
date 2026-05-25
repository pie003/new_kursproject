/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Request;

/**
 *
 * @author Ирина
 */
public enum RequestStatus {
    PENDING("pending", "Обработка", "Запрос обрабатывается"),
    SUCCESS("success", "Готово", "Текст успешно сгенерирован"),
    FAILED("failed", "Ошибка", "Произошла ошибка при генерации"),
    RETRY("retry", "Повтор", "Требуется повторная попытка");
    
    private final String code;
    private final String icon;
    private final String description;
    
    RequestStatus(String code, String icon, String description) {
        this.code = code;
        this.icon = icon;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public String getDescription() {
        return description;
    }  
    
    public static RequestStatus fromCode(String code) {
        if (code == null) {
            return PENDING;
        }
        
        for (RequestStatus tone : values()) {
            if (tone.code.equalsIgnoreCase(code)) {
                return tone;
            }
        }
        return PENDING;
    }
}
