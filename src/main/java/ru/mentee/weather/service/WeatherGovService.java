/* @MENTEE_POWER (C)2026 */
package ru.mentee.weather.service;

import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.mentee.weather.model.Status;
import ru.mentee.weather.model.WeatherReport;

@Service
public class WeatherGovService {

    @Async("taskExecutor")
    public CompletableFuture<WeatherReport> getWeather(String city) {
        try {
            // Имитация сетевой задержки
            Thread.sleep(500);
            return CompletableFuture.completedFuture(
                    WeatherReport.builder()
                            .source("WeatherGov")
                            .temperature(15.0)
                            .humidity(65.0)
                            .windSpeed(10.0)
                            .status(Status.SUCCESS)
                            .build());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture(
                    WeatherReport.builder()
                            .source("WeatherGov")
                            .status(Status.ERROR)
                            .errorMessage("Interrupted: " + e.getMessage())
                            .build());
        }
    }
}
