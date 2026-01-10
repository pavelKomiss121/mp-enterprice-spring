/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // В реальном приложении здесь был бы SecurityContext
        // Для тестов возвращаем "system"
        return () -> Optional.of("system");
    }
}
