package com.hotel.reservation.service;

import com.hotel.reservation.dto.CreateReservationRequest;
import com.hotel.reservation.dto.ReservationResponse;
import com.hotel.reservation.dto.RoomResponse;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.Room;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HotelService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public HotelService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> searchRooms(String category, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        return roomRepository.findAll().stream()
                .filter(room -> category == null || category.isBlank() || room.getCategory().equalsIgnoreCase(category.trim()))
                .map(room -> RoomResponse.from(room, !hasConflict(room, checkIn, checkOut)))
                .toList();
    }

    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        String guestName = request.guestName().trim();
        if (guestName.isEmpty() || guestName.length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guest name must be between 1 and 120 characters.");
        }
        validateDates(request.checkInDate(), request.checkOutDate());

        Room room = roomRepository.findByRoomNumberForUpdate(request.roomNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found."));
        if (hasConflict(room, request.checkInDate(), request.checkOutDate())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That room is already booked for the selected dates.");
        }
        Reservation saved = reservationRepository.save(new Reservation(guestName, room, request.checkInDate(), request.checkOutDate()));
        return ReservationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations() {
        return reservationRepository.findAllByOrderByCheckInDateDesc().stream()
                .map(ReservationResponse::from).toList();
    }

    @Transactional
    public void cancelReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found.");
        }
        reservationRepository.deleteById(id);
    }

    private boolean hasConflict(Room room, LocalDate checkIn, LocalDate checkOut) {
        return reservationRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                room.getId(), checkOut, checkIn);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out must be after check-in.");
        }
    }
}
