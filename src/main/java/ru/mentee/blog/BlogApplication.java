/* @MENTEE_POWER (C)2026 */
package ru.mentee.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        // Устанавливаем профиль "blog" программно
        System.setProperty("spring.profiles.active", "blog");
        SpringApplication.run(BlogApplication.class, args);
    }
}
