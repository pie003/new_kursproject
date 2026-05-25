/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package new_project.mew_project;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Ирина
 */
public class LogManager {
    private static final String LOG_FILE = "logs.log";
    
    public static void logError(String message, Exception e) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pw.println("[" + LocalDateTime.now().format(formatter) + "] " + message);
            pw.println("Exception: " + e.toString());
            for (StackTraceElement element : e.getStackTrace()) {
                pw.println("  at " + element.toString());
            }
            pw.println("---");
            
        } catch (Exception ex) {
            System.err.println("Failed to write log: " + ex.getMessage());
        }
        
        System.err.println("[ERROR] " + message);
        e.printStackTrace();
    }
    
    public static void logInfo(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pw.println("[" + LocalDateTime.now().format(formatter) + "] INFO: " + message);
            
        } catch (Exception e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
        System.out.println("[INFO] " + message);
    }
}
