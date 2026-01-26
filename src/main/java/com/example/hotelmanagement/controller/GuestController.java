package com.example.hotelmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.repository.GuestRepository;

@RestController
@RequestMapping("/api/v1/guests")
public class GuestController {

    @Autowired
    private GuestRepository guestRepository; // Dùng tạm Repository cho nhanh

    // 1. Tạo khách mới
    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        return guestRepository.save(guest);
    }

    // 2. Lấy danh sách khách CÓ PHÂN TRANG (Sửa theo yêu cầu 7.2)
    // GET /api/v1/guests?page=0&size=10
    @GetMapping
    public Page<Guest> getAllGuests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return guestRepository.findAll(pageable);
    }

    // 3. Lấy khách theo ID
    @GetMapping("/{id}")
    public Guest getGuestById(@PathVariable Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách không tồn tại"));
    }

    // 4. Sửa thông tin khách
    @PutMapping("/{id}")
    public Guest updateGuest(@PathVariable Long id, @RequestBody Guest updateInfo) {
        Guest guest = guestRepository.findById(id).orElseThrow();
        guest.setFirstName(updateInfo.getFirstName());
        guest.setLastName(updateInfo.getLastName());
        guest.setEmail(updateInfo.getEmail());
        guest.setPhone(updateInfo.getPhone());
        return guestRepository.save(guest);
    }
    
    // 5. Xóa khách (Soft delete - tạm thời dùng Hard delete cho đơn giản)
    @DeleteMapping("/{id}")
    public void deleteGuest(@PathVariable Long id) {
        guestRepository.deleteById(id);
    }
}