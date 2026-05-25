/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package new_project.mew_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author Ирина
 */

@SpringBootApplication
@ComponentScan(basePackages = {
    "new_project.mew_project",  
    "Services",                  
    "main_object.Excuse",
    "main_object.Request",
    "main_object.User",
    "AppController",
    "db"
})
public class Mew_project {

    public static void main(String[] args) {
        SpringApplication.run(Mew_project.class, args);
    }
}
