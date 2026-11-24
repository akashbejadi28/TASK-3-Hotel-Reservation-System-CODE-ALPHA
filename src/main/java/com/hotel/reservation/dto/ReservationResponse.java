package com.hotel.reservation.dto;

import com.hotel.reservation.model.Reservation;
import java.time.LocalDate;

public record ReservationResponse(Long id, String guestName, Integer roomNumber, String category,
                                  LocalDate checkInDate, LocalDate checkOutDate, double totalAmount) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getGuestName(),
                reservation.getRoom().getRoomNumber(), reservation.getRoom().getCategory(),
                reservation.getCheckInDate(), reservation.getCheckOutDate(), reservation.getTotalAmount());
    }
}
