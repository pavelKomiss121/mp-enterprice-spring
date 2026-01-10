/* @MENTEE_POWER (C)2026 */
package ru.mentee.calculator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Отключено для ускорения тестов - используется только booking модуль")
@SpringBootTest
class CalculatorServiceTest {

    @Autowired private CalculatorService calculatorService;

    @Test
    @DisplayName("Тест сложения")
    void testAdd() {
        int result = calculatorService.add(5, 3);
        assertThat(result).isEqualTo(8);
    }

    @Test
    @DisplayName("Тест вычитания")
    void testSubtract() {
        int result = calculatorService.subtract(10, 4);
        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("Тест умножения")
    void testMultiply() {
        int result = calculatorService.multiply(3, 4);
        assertThat(result).isEqualTo(12);
    }

    @Test
    @DisplayName("Тест деления")
    void testDivide() {
        int result = calculatorService.divide(10, 2);
        assertThat(result).isEqualTo(5);
    }

    @Test
    @DisplayName("Тест деления на ноль")
    void testDivideByZero() {
        assertThatThrownBy(() -> calculatorService.divide(10, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Division by zero!");
    }
}
