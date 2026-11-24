package com.hotel.reservation.dto;

import com.hotel.reservation.model.Room;

public record RoomResponse(Integer roomNumber, String category, double pricePerNight, boolean available) {
    public static RoomResponse from(Room room, boolean available) {
        return new RoomResponse(room.getRoomNumber(), room.getCategory(), room.getPricePerNight(), available);
    }
}
