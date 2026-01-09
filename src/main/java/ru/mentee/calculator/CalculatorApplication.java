/* @MENTEE_POWER (C)2026 */
package ru.mentee.calculator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import ru.mentee.calculator.service.CalculatorService;

@SpringBootApplication(
        exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            LiquibaseAutoConfiguration.class
        })
public class CalculatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(CalculatorService calculatorService) {
        return args -> {
            System.out.println("\n=== Демонстрация работы AOP ===\n");

            System.out.println("1. Сложение:");
            int sum = calculatorService.add(10, 5);
            System.out.println("   Результат: " + sum + "\n");

            System.out.println("2. Вычитание:");
            int diff = calculatorService.subtract(10, 5);
            System.out.println("   Результат: " + diff + "\n");

            System.out.println("3. Умножение:");
            int product = calculatorService.multiply(10, 5);
            System.out.println("   Результат: " + product + "\n");

            System.out.println("4. Деление:");
            int quotient = calculatorService.divide(10, 5);
            System.out.println("   Результат: " + quotient + "\n");

            System.out.println("=== Смотрите логи выше - там видна работа аспектов ===\n");
        };
    }
}
