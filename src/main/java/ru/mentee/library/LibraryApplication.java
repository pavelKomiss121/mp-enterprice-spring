/* @MENTEE_POWER (C)2026 */
package ru.mentee.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {
    public static void main(String[] args) {
        // Устанавливаем профиль "library" программно
        System.setProperty("spring.profiles.active", "library");
        SpringApplication.run(LibraryApplication.class, args);
    }
}
