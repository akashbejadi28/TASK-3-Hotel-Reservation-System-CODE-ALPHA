package com.hotel.reservation.controller;

import com.hotel.reservation.dto.CreateReservationRequest;
import com.hotel.reservation.dto.ReservationResponse;
import com.hotel.reservation.dto.RoomResponse;
import com.hotel.reservation.service.HotelService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) { this.hotelService = hotelService; }

    @GetMapping("/rooms")
    public List<RoomResponse> searchRooms(
            @RequestParam(required = false) String category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return hotelService.searchRooms(category, checkIn, checkOut);
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> getReservations() { return hotelService.getReservations(); }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@Valid @RequestBody CreateReservationRequest request) {
        return hotelService.createReservation(request);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@PathVariable Long id) { hotelService.cancelReservation(id); }
}
