package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    // Tạo loại phòng (Ví dụ: POST /api/v1/rooms/types)
    @PostMapping("/types")
    public RoomType createType(@RequestBody RoomType type) {
        return roomService.createRoomType(type);
    }

    // Tạo phòng mới (Ví dụ: POST /api/v1/rooms?typeId=1)
    @PostMapping
    public Room createRoom(@RequestBody Room room, @RequestParam Long typeId) {
        return roomService.createRoom(room, typeId);
    }

    // Lấy danh sách phòng
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }
}