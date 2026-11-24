package com.hotel.reservation.repository;

import com.hotel.reservation.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByOrderByCheckInDateDesc();
    boolean existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Long roomId, LocalDate requestedCheckOut, LocalDate requestedCheckIn);
}
