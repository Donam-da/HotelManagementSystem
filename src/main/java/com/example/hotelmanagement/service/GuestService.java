package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.exception.BusinessException;
import com.example.hotelmanagement.exception.ResourceNotFoundException;
import com.example.hotelmanagement.dto.GuestDTO;
import com.example.hotelmanagement.repository.GuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    // UC-001: Register Guest
    @Transactional
    public Guest registerGuest(Guest guest) {
        // BR-102: Email address must be unique
        if (guestRepository.existsByEmail(guest.getEmail())) {
            throw new BusinessException("Email này đã được sử dụng trong hệ thống!");
        }
        return guestRepository.save(guest);
    }

    @Transactional
    public Guest updateGuestProfile(Long id, Guest guestDetails) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại với ID: " + id));
        
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
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại với ID: " + id));
    }

    @Transactional
    public void deleteGuest(Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại với ID: " + id));
        
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
    public Page<Guest> getAllGuests(Pageable pageable, boolean includeDeleted) {
        if (includeDeleted) {
            return guestRepository.findAll(pageable);
        }
        // Chỉ lấy những khách chưa bị xóa
        return guestRepository.findByIsDeletedFalse(pageable);
    }

    // --- MAPPING ENTITY TO DTO (Hỗ trợ mục 9.1.1) ---
    public GuestDTO convertToDTO(Guest guest) {
        GuestDTO dto = new GuestDTO();
        dto.setId(guest.getId());
        dto.setFirstName(guest.getFirstName());
        dto.setLastName(guest.getLastName());
        dto.setEmail(guest.getEmail());
        dto.setPhone(guest.getPhone());
        dto.setAddress(guest.getAddress());
        dto.setIdNumber(guest.getIdNumber());
        dto.setDateOfBirth(guest.getDateOfBirth());
        dto.setPreferences(guest.getPreferences());
        dto.setLoyaltyPoints(guest.getLoyaltyPoints());
        return dto;
    }

    public Guest convertToEntity(GuestDTO dto) {
        Guest guest = new Guest();
        guest.setFirstName(dto.getFirstName());
        guest.setLastName(dto.getLastName());
        guest.setEmail(dto.getEmail());
        guest.setPhone(dto.getPhone());
        guest.setAddress(dto.getAddress());
        guest.setIdNumber(dto.getIdNumber());
        guest.setDateOfBirth(dto.getDateOfBirth());
        guest.setPreferences(dto.getPreferences());
        return guest;
    }
}