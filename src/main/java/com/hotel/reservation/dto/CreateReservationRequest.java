package com.hotel.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateReservationRequest(
        @NotBlank String guestName,
        @NotNull @Positive Integer roomNumber,
        @NotNull LocalDate checkInDate,
        @NotNull LocalDate checkOutDate) { }
