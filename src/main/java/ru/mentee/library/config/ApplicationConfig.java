/* @MENTEE_POWER (C)2025 */
package ru.mentee.library.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ApplicationConfig {

    // Пример использования различных scope
    // BookService создается автоматически через @Service аннотацию с singleton scope

    // Пример prototype бина (например, для корзины покупок)
    // Каждый раз при запросе этого бина будет создаваться новый экземпляр
    @Bean
    @Scope("prototype")
    @Qualifier("shoppingCart") public Object shoppingCart() {
        return new Object();
    }

    // Пример singleton бина (по умолчанию)
    @Bean
    @Scope("singleton")
    @Qualifier("exampleSingleton") public Object exampleSingleton() {
        return new Object();
    }
}
