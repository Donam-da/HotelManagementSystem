package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Bắt buộc phải có để Spring hiểu đây là lớp Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    // Hàm 1: Lấy danh sách tất cả khách hàng
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    // Hàm 2: Tạo khách hàng mới
    public Guest createGuest(Guest guest) {
        // Kiểm tra xem email đã tồn tại chưa (logic nghiệp vụ)
        if (guestRepository.existsByEmail(guest.getEmail())) {
            throw new RuntimeException("Email " + guest.getEmail() + " đã tồn tại!");
        }
        return guestRepository.save(guest);
    }
}