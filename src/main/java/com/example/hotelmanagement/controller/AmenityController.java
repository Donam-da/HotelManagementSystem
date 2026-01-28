package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Amenity;
import com.example.hotelmanagement.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amenities")
public class AmenityController {

    private final RoomService roomService;

    public AmenityController(RoomService roomService) {
        this.roomService = roomService;
    }

    // 1. Lấy danh sách tiện nghi
    @GetMapping
    public List<Amenity> getAllAmenities() {
        return roomService.getAllAmenities();
    }

    // 2. Tạo tiện nghi mới (Ví dụ: Wifi, Pool, Gym)
    @PostMapping
    public Amenity createAmenity(@RequestBody Amenity amenity) {
        return roomService.createAmenity(amenity);
    }

    // 3. Xóa tiện nghi
    @DeleteMapping("/{id}")
    public void deleteAmenity(@PathVariable Long id) {
        roomService.deleteAmenity(id);
    }
    
    // 4. Cập nhật tiện nghi
    @PutMapping("/{id}")
    public Amenity updateAmenity(@PathVariable Long id, @RequestBody Amenity details) {
        return roomService.updateAmenity(id, details);
    }
}