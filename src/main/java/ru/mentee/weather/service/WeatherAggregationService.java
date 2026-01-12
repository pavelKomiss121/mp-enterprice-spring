/* @MENTEE_POWER (C)2026 */
package ru.mentee.weather.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentee.weather.model.AggregatedWeatherReport;
import ru.mentee.weather.model.Status;
import ru.mentee.weather.model.WeatherReport;

@Service
@RequiredArgsConstructor
public class WeatherAggregationService {

    private static final int TIMEOUT_SECONDS = 2;

    private final WeatherGovService weatherGovService;
    private final OpenWeatherService openWeatherService;
    private final AccuWeatherService accuWeatherService;

    public CompletableFuture<AggregatedWeatherReport> getAggregatedReport(String city) {
        CompletableFuture<WeatherReport> govFuture = weatherGovService.getWeather(city);
        CompletableFuture<WeatherReport> openFuture = openWeatherService.getWeather(city);
        CompletableFuture<WeatherReport> accuFuture = accuWeatherService.getWeather(city);

        CompletableFuture<WeatherReport> govReport =
                handleFutureWithTimeout(govFuture, "WeatherGov");
        CompletableFuture<WeatherReport> openReport =
                handleFutureWithTimeout(openFuture, "OpenWeather");
        CompletableFuture<WeatherReport> accuReport =
                handleFutureWithTimeout(accuFuture, "AccuWeather");

        return CompletableFuture.allOf(govReport, openReport, accuReport)
                .thenApply(
                        v -> {
                            List<WeatherReport> reports = new ArrayList<>();
                            try {
                                reports.add(govReport.get());
                                reports.add(openReport.get());
                                reports.add(accuReport.get());
                            } catch (Exception e) {
                                // Should not happen as all futures are completed
                            }
                            return AggregatedWeatherReport.builder()
                                    .city(city)
                                    .reports(reports)
                                    .build();
                        });
    }

    private CompletableFuture<WeatherReport> handleFutureWithTimeout(
            CompletableFuture<WeatherReport> future, String source) {
        return future.orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .handle(
                        (result, throwable) -> {
                            if (throwable != null) {
                                if (throwable instanceof java.util.concurrent.TimeoutException) {
                                    return WeatherReport.builder()
                                            .source(source)
                                            .status(Status.TIMEOUT)
                                            .errorMessage(
                                                    "Request timeout after "
                                                            + TIMEOUT_SECONDS
                                                            + " seconds")
                                            .build();
                                } else {
                                    return WeatherReport.builder()
                                            .source(source)
                                            .status(Status.ERROR)
                                            .errorMessage("Error: " + throwable.getMessage())
                                            .build();
                                }
                            }
                            return result;
                        });
    }
}
