package com.hotel.reservation.repository;

import com.hotel.reservation.model.Room;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(Integer roomNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.roomNumber = :roomNumber")
    Optional<Room> findByRoomNumberForUpdate(@Param("roomNumber") Integer roomNumber);
}
