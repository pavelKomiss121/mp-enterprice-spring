/* @MENTEE_POWER (C)2026 */
package ru.mentee.weather.service;

import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.mentee.weather.model.Status;
import ru.mentee.weather.model.WeatherReport;

@Service
public class AccuWeatherService {

    @Async("taskExecutor")
    public CompletableFuture<WeatherReport> getWeather(String city) {
        try {
            // Имитация сетевой задержки
            Thread.sleep(700);
            return CompletableFuture.completedFuture(
                    WeatherReport.builder()
                            .source("AccuWeather")
                            .temperature(14.5)
                            .humidity(68.0)
                            .windSpeed(11.0)
                            .status(Status.SUCCESS)
                            .build());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture(
                    WeatherReport.builder()
                            .source("AccuWeather")
                            .status(Status.ERROR)
                            .errorMessage("Interrupted: " + e.getMessage())
                            .build());
        }
    }
}
