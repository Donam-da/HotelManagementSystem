package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // 1. Tạo loại phòng (Admin dùng)
    // POST /api/v1/rooms/types
    @PostMapping("/types")
    public RoomType createType(@RequestBody RoomType type) {
        return roomService.createRoomType(type);
    }

    // 2. Tạo phòng mới (Admin dùng)
    // POST /api/v1/rooms?typeId=1
    @PostMapping
    public Room createRoom(@RequestBody Room room, @RequestParam Long typeId) {
        return roomService.createRoom(room, typeId);
    }

    // 3. Lấy danh sách phòng CÓ PHÂN TRANG (Chuẩn NFR 5.2)
    // GET /api/v1/rooms?page=0&size=5
    @GetMapping
    public Page<Room> getAllRooms(
            @RequestParam(defaultValue = "0") int page, // Trang số mấy (bắt đầu từ 0)
            @RequestParam(defaultValue = "10") int size // Lấy bao nhiêu dòng
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return roomService.getAllRooms(pageable);
    }

    // 4. Tìm phòng trống (UC-002)
    // GET /api/v1/rooms/available?checkIn=2026-05-01&checkOut=2026-05-05
    @GetMapping("/available")
    public List<Room> findAvailableRooms(
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut) {
        return roomService.getAvailableRooms(checkIn, checkOut);
    }

    // API phụ: Gán tiện nghi vào loại phòng (Ví dụ: Thêm Wifi vào phòng Deluxe)
    // POST /api/v1/rooms/types/{typeId}/amenities/{amenityId}
    @PostMapping("/types/{typeId}/amenities/{amenityId}")
    public RoomType addAmenityToType(@PathVariable Long typeId, @PathVariable Long amenityId) {
        return roomService.addAmenityToType(typeId, amenityId);
    }
}