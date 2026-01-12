/* @MENTEE_POWER (C)2026 */
package ru.mentee.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherReport {
    private String source;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private Status status;
    private String errorMessage;
}
