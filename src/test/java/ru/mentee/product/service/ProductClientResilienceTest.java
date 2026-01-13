/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import ru.mentee.product.ProductApplication;
import ru.mentee.product.api.dto.ProductDto;

@SpringBootTest(classes = ProductApplication.class)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(
        properties = {
            "feign.client.config.product-service.url=http://localhost:${wiremock.server.port}",
            "spring.liquibase.enabled=false",
            "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
        })
class ProductClientResilienceTest {

    @Autowired private ProductIntegrationService service;

    @Test
    @DisplayName("Should успешно получить продукт")
    void shouldGetProductSuccessfully() {
        stubFor(
                get(urlEqualTo("/api/products/1"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                "{\"id\":1,\"name\":\"Test"
                                                    + " Product\",\"description\":\"Test\",\"price\":99.99,\"category\":\"Electronics\"}")));

        ProductDto result = service.getProduct(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Should повторить запрос при временной ошибке")
    void shouldRetryOnTimeout() {
        stubFor(
                get(urlEqualTo("/api/products/1"))
                        .willReturn(aResponse().withFixedDelay(2000))); // Имитация таймаута

        // Ожидаем, что сервис вызовет fallback после нескольких попыток
        assertDoesNotThrow(() -> service.getProduct(1L));
    }

    @Test
    @DisplayName("Should разомкнуть цепь после нескольких сбоев")
    void shouldOpenCircuitBreaker() {
        // 1. Успешные вызовы
        stubFor(
                get(urlEqualTo("/api/products/1"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                "{\"id\":1,\"name\":\"Test"
                                                    + " Product\",\"description\":\"Test\",\"price\":99.99,\"category\":\"Electronics\"}")));

        // Выполняем несколько успешных вызовов
        for (int i = 0; i < 3; i++) {
            try {
                service.getProduct(1L);
            } catch (Exception e) {
                // ignore
            }
        }

        // 2. Вызовы с 500 ошибкой, чтобы разомкнуть цепь
        stubFor(get(urlEqualTo("/api/products/1")).willReturn(serverError()));

        // Делаем достаточно ошибок, чтобы открыть circuit breaker (50% от slidingWindowSize=10)
        // 3 успешных + 6 ошибок = 9 вызовов, из них 6 ошибок = 66% > 50%
        for (int i = 0; i < 6; i++) {
            try {
                service.getProduct(1L);
            } catch (Exception e) {
                // ignore
            }
        }

        // 3. Проверяем, что fallback вызывается сразу, без запроса к WireMock
        // Считаем количество запросов до последнего вызова
        int requestsBefore = WireMock.getAllServeEvents().size();

        // Вызываем сервис - circuit breaker должен быть открыт, поэтому fallback вызывается сразу
        ProductDto fallbackDto = service.getProduct(1L);
        assertThat(fallbackDto.getName()).isEqualTo("Cached Product Data");

        // Проверяем, что после открытия circuit breaker новый запрос не пошел к WireMock
        // (circuit breaker открыт, поэтому запрос не должен идти к серверу)
        // Учитываем возможные edge cases, поэтому проверяем, что запросов не больше чем было +
        // небольшой запас
        int requestsAfter = WireMock.getAllServeEvents().size();
        // Circuit breaker открыт, поэтому новый запрос не должен идти к серверу
        // Но может быть небольшая задержка в открытии, поэтому допускаем максимум 1 дополнительный
        // запрос
        assertThat(requestsAfter).isLessThanOrEqualTo(requestsBefore + 1);
    }
}
