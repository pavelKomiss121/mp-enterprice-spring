/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Event ID is required") private Long eventId;

    @NotNull(message = "Seat ID is required") private Long seatId;

    @NotNull(message = "User ID is required") private Long userId;
}
