package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.exception.ResourceNotFoundException; // <-- Quan trọng: Import lỗi mới
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

    // 1. Tạo loại phòng mới (Ví dụ: Deluxe, Standard)
    public RoomType createRoomType(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    // 2. Thêm phòng mới vào loại phòng (Đã nâng cấp xử lý lỗi)
    public Room createRoom(Room room, Long roomTypeId) {
        RoomType type = roomTypeRepository.findById(roomTypeId)
                // SỬ DỤNG CUSTOM EXCEPTION ĐỂ TRẢ VỀ MÃ 404
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + roomTypeId));
        
        room.setRoomType(type);
        return roomRepository.save(room);
    }

    // 3. Lấy danh sách phòng CÓ PHÂN TRANG (Đáp ứng yêu cầu Non-Functional 5.2)
    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    // 4. Tìm phòng trống (Tính năng UC-002)
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRooms(checkIn, checkOut);
    }
}