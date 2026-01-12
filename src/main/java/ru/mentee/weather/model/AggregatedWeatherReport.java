/* @MENTEE_POWER (C)2026 */
package ru.mentee.weather.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedWeatherReport {
    private String city;
    private List<WeatherReport> reports;
}
