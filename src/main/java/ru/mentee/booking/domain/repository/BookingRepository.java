/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.booking.domain.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {}
