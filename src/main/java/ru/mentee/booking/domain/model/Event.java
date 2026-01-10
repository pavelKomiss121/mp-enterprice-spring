/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "booked_seats")
    @Builder.Default
    private Integer bookedSeats = 0;

    @Version private Long version; // Для оптимистической блокировки
}
