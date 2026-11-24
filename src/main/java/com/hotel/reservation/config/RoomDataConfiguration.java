package com.hotel.reservation.config;

import com.hotel.reservation.model.Room;
import com.hotel.reservation.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoomDataConfiguration {
    @Bean
    CommandLineRunner seedRooms(RoomRepository rooms) {
        return args -> {
            if (rooms.count() == 0) {
                rooms.save(new Room(101, "Standard", 100.00));
                rooms.save(new Room(102, "Standard", 100.00));
                rooms.save(new Room(201, "Deluxe", 150.00));
                rooms.save(new Room(202, "Deluxe", 150.00));
                rooms.save(new Room(301, "Suite", 200.00));
                rooms.save(new Room(302, "Suite", 200.00));
            }
        };
    }
}
