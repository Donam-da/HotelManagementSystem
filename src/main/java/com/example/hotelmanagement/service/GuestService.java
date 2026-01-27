package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    // UC-001: Register Guest
    public Guest registerGuest(Guest guest) {
        // Có thể thêm validate email trùng lặp tại đây
        return guestRepository.save(guest);
    }

    public Guest updateGuestProfile(Long id, Guest guestDetails) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        
        guest.setFirstName(guestDetails.getFirstName());
        guest.setLastName(guestDetails.getLastName());
        guest.setPhone(guestDetails.getPhone());
        guest.setAddress(guestDetails.getAddress());
        guest.setIdNumber(guestDetails.getIdNumber());
        guest.setPreferences(guestDetails.getPreferences()); // Cập nhật sở thích
        
        return guestRepository.save(guest);
    }

    // --- BỔ SUNG THEO YÊU CẦU 4.2.1 (Read & Delete) ---
    public Guest getGuestById(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
    }

    public void deleteGuest(Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        
        // THAY ĐỔI: Soft Delete (Yêu cầu 7.2)
        // Thay vì xóa khỏi DB, ta chỉ đánh dấu là đã xóa
        guest.setDeleted(true);
        guestRepository.save(guest);
    }

    // --- BỔ SUNG THEO YÊU CẦU 4.2.1 (Search) ---
    public List<Guest> searchGuests(String keyword) {
        return guestRepository.searchGuests(keyword);
    }

    // --- BỔ SUNG CHO CONTROLLER (Layered Architecture) ---
    public Page<Guest> getAllGuests(Pageable pageable) {
        // Chỉ lấy những khách chưa bị xóa
        return guestRepository.findByIsDeletedFalse(pageable);
    }
}