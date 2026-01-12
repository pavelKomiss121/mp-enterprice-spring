/* @MENTEE_POWER (C)2026 */
package ru.mentee.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import ru.mentee.weather.model.AggregatedWeatherReport;
import ru.mentee.weather.model.Status;
import ru.mentee.weather.model.WeatherReport;

@SpringBootTest(classes = ru.mentee.weather.WeatherApplication.class)
@TestPropertySource(
        properties = {
            "spring.liquibase.enabled=false",
            "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
        })
class WeatherAggregationServiceTest {

    @Autowired private WeatherAggregationService aggregationService;

    @MockBean private WeatherGovService weatherGovService;

    @MockBean private OpenWeatherService openWeatherService;

    @MockBean private AccuWeatherService accuWeatherService;

    @Test
    @DisplayName("Should агрегировать успешные ответы от всех сервисов")
    void shouldAggregateSuccessResponses() throws Exception {
        // Given
        when(weatherGovService.getWeather("London"))
                .thenReturn(
                        CompletableFuture.completedFuture(
                                WeatherReport.builder()
                                        .source("WeatherGov")
                                        .temperature(15.0)
                                        .humidity(65.0)
                                        .windSpeed(10.0)
                                        .status(Status.SUCCESS)
                                        .build()));
        when(openWeatherService.getWeather("London"))
                .thenReturn(
                        CompletableFuture.completedFuture(
                                WeatherReport.builder()
                                        .source("OpenWeather")
                                        .temperature(16.0)
                                        .humidity(70.0)
                                        .windSpeed(12.0)
                                        .status(Status.SUCCESS)
                                        .build()));
        when(accuWeatherService.getWeather("London"))
                .thenReturn(
                        CompletableFuture.completedFuture(
                                WeatherReport.builder()
                                        .source("AccuWeather")
                                        .temperature(14.5)
                                        .humidity(68.0)
                                        .windSpeed(11.0)
                                        .status(Status.SUCCESS)
                                        .build()));

        // When
        CompletableFuture<AggregatedWeatherReport> future =
                aggregationService.getAggregatedReport("London");

        // Then
        AggregatedWeatherReport report = future.get(5, TimeUnit.SECONDS);
        assertThat(report.getReports()).hasSize(3);
        assertThat(report.getReports()).allMatch(r -> r.getStatus() == Status.SUCCESS);
    }

    @Test
    @DisplayName("Should обработать таймаут одного из сервисов")
    void shouldHandleTimeoutFromOneService() throws Exception {
        // Given
        when(weatherGovService.getWeather("Paris"))
                .thenReturn(
                        CompletableFuture.supplyAsync(
                                () -> {
                                    // Имитация таймаута
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    return WeatherReport.builder()
                                            .source("WeatherGov")
                                            .temperature(15.0)
                                            .humidity(65.0)
                                            .windSpeed(10.0)
                                            .status(Status.SUCCESS)
                                            .build();
                                }));
        when(openWeatherService.getWeather("Paris"))
                .thenReturn(
                        CompletableFuture.completedFuture(
                                WeatherReport.builder()
                                        .source("OpenWeather")
                                        .temperature(16.0)
                                        .humidity(70.0)
                                        .windSpeed(12.0)
                                        .status(Status.SUCCESS)
                                        .build()));
        when(accuWeatherService.getWeather("Paris"))
                .thenReturn(
                        CompletableFuture.completedFuture(
                                WeatherReport.builder()
                                        .source("AccuWeather")
                                        .temperature(14.5)
                                        .humidity(68.0)
                                        .windSpeed(11.0)
                                        .status(Status.SUCCESS)
                                        .build()));

        // When
        CompletableFuture<AggregatedWeatherReport> future =
                aggregationService.getAggregatedReport("Paris");

        // Then
        AggregatedWeatherReport report = future.get();
        assertThat(report.getReports()).hasSize(3);
        assertThat(report.getReports())
                .filteredOn(r -> r.getSource().equals("WeatherGov"))
                .allMatch(r -> r.getStatus() == Status.TIMEOUT);
        assertThat(report.getReports())
                .filteredOn(r -> !r.getSource().equals("WeatherGov"))
                .allMatch(r -> r.getStatus() == Status.SUCCESS);
    }
}
