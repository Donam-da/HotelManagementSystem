package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Amenity;
import com.example.hotelmanagement.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amenities")
public class AmenityController {

    @Autowired
    private AmenityRepository amenityRepository;

    // 1. Lấy danh sách tiện nghi
    @GetMapping
    public List<Amenity> getAllAmenities() {
        return amenityRepository.findAll();
    }

    // 2. Tạo tiện nghi mới (Ví dụ: Wifi, Pool, Gym)
    @PostMapping
    public Amenity createAmenity(@RequestBody Amenity amenity) {
        return amenityRepository.save(amenity);
    }

    // 3. Xóa tiện nghi
    @DeleteMapping("/{id}")
    public void deleteAmenity(@PathVariable Long id) {
        amenityRepository.deleteById(id);
    }
    
    // 4. Cập nhật tiện nghi
    @PutMapping("/{id}")
    public Amenity updateAmenity(@PathVariable Long id, @RequestBody Amenity details) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tiện nghi không tồn tại"));
        
        amenity.setName(details.getName());
        amenity.setDescription(details.getDescription());
        amenity.setIcon(details.getIcon());
        return amenityRepository.save(amenity);
    }
}