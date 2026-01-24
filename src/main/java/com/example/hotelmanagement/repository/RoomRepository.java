package com.example.hotelmanagement.repository;
import com.example.hotelmanagement.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Tìm các phòng theo ID loại phòng
    List<Room> findByRoomTypeId(Long roomTypeId);
}