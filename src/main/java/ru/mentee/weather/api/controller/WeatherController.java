/* @MENTEE_POWER (C)2026 */
package ru.mentee.weather.api.controller;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.weather.model.AggregatedWeatherReport;
import ru.mentee.weather.service.WeatherAggregationService;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherAggregationService aggregationService;

    @GetMapping("/{city}")
    public ResponseEntity<AggregatedWeatherReport> getWeather(@PathVariable String city) {
        try {
            CompletableFuture<AggregatedWeatherReport> future =
                    aggregationService.getAggregatedReport(city);
            AggregatedWeatherReport report = future.get();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
