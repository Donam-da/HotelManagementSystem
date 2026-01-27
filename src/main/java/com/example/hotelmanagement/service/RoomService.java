package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.repository.RoomRepository;
import com.example.hotelmanagement.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    // UC-002: Search Available Rooms (Nâng cao)
    public List<Room> searchRooms(LocalDate checkIn, LocalDate checkOut, Long typeId, Integer capacity) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Ngày check-in và check-out là bắt buộc");
        }
        return roomRepository.searchAvailableRooms(checkIn, checkOut, typeId, capacity);
    }

    // UC-011: Manage Room Status (Cập nhật trạng thái thủ công)
    public Room updateRoomStatus(Long roomId, String status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
        
        // Các trạng thái hợp lệ: AVAILABLE, OCCUPIED, MAINTENANCE, DIRTY
        room.setStatus(status);
        return roomRepository.save(room);
    }
    
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Hỗ trợ phân trang cho Controller
    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public RoomType createRoomType(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    public Room createRoom(Room room, Long roomTypeId) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("Loại phòng không tồn tại"));
        room.setRoomType(roomType);
        return roomRepository.save(room);
    }

    // Tìm phòng trống cơ bản (chỉ theo ngày)
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRooms(checkIn, checkOut);
    }
}