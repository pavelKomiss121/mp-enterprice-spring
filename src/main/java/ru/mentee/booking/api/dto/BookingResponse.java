/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentee.booking.domain.model.BookingStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long bookingId;
    private BookingStatus status;
    private String seatNumber;
    private String eventName;
}
