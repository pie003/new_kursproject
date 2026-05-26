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
    DRAFT("draft", "Черновик"),
    COMPLETED("completed", "Завершено"),
    SAVED("saved", "Сохранено"),
    FAILED("failed", "Ошибка");
    
    private final String code;
    private final String displayName;
    
    RequestStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    
    public static RequestStatus fromCode(String code) {
        for (RequestStatus s : values()) {
            if (s.code.equalsIgnoreCase(code)) return s;
        }
        return DRAFT;
    }
}
