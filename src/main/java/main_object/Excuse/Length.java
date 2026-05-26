/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Excuse;

/**
 *
 * @author Ирина
 */
public enum Length {
    SHORT("SHORT", "Кратко (2-3 предложения)"),
    MEDIUM("MEDIUM", "Средний (абзац)"),
    LONG("LONG", "Развёрнуто (несколько абзацев)");

    private final String code;
    private final String displayName;

    Length(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }

    public static Length fromCode(String code) {
        for (Length l : values()) {
            if (l.code.equalsIgnoreCase(code)) return l;
        }
        return MEDIUM;
    }
}