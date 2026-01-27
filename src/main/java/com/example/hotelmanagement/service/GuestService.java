package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.exception.BusinessException;
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
        // BR-102: Email address must be unique
        if (guestRepository.existsByEmail(guest.getEmail())) {
            throw new BusinessException("Email này đã được sử dụng trong hệ thống!");
        }
        return guestRepository.save(guest);
    }

    public Guest updateGuestProfile(Long id, Guest guestDetails) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        
        // BR-102: Check email uniqueness if changed
        if (guestDetails.getEmail() != null && !guestDetails.getEmail().equals(guest.getEmail())) {
             if (guestRepository.existsByEmail(guestDetails.getEmail())) {
                 throw new BusinessException("Email này đã được sử dụng!");
             }
             guest.setEmail(guestDetails.getEmail());
        }

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