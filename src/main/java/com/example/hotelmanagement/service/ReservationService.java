package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private GuestRepository guestRepository;

    public Reservation createReservation(Reservation reservation, Long guestId) {
        // 1. Kiểm tra ngày Check-in phải là tương lai
        if (reservation.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Ngày check-in phải từ hôm nay trở đi!");
        }

        // 2. Tìm khách hàng
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại!"));
        
        // 3. Gán thông tin và lưu
        reservation.setGuest(guest);
        reservation.setStatus("CONFIRMED");
        return reservationRepository.save(reservation);
    }
}