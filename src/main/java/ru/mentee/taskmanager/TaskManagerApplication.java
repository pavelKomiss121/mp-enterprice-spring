/* @MENTEE_POWER (C)2026 */
package ru.mentee.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ru.mentee.taskmanager")
@EntityScan("ru.mentee.taskmanager.domain.model")
@EnableJpaRepositories("ru.mentee.taskmanager.domain.repository")
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
