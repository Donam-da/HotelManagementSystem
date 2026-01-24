package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.repository.RoomRepository;
import com.example.hotelmanagement.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    // 1. Tạo loại phòng mới (Ví dụ: Deluxe)
    public RoomType createRoomType(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    // 2. Thêm phòng mới vào loại phòng
    public Room createRoom(Room room, Long roomTypeId) {
        RoomType type = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng!"));
        room.setRoomType(type);
        return roomRepository.save(room);
    }

    // 3. Lấy danh sách phòng
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}