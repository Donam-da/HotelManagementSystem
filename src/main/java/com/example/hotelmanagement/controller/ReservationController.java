package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.exception.ResourceNotFoundException;
import com.example.hotelmanagement.repository.ReservationRepository;
import com.example.hotelmanagement.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate; // Import LocalDate

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository; // Thêm cái này để tìm nhanh

    // 1. Tạo đơn đặt phòng (POST)
    @PostMapping
    public Reservation createReservation(
            @RequestBody Reservation reservation,
            @RequestParam Long guestId,
            @RequestParam Long roomId) {
        return reservationService.createReservation(reservation, guestId, roomId);
    }

    // 2. Lấy chi tiết đơn (GET /api/v1/reservations/{id})
    // [SỬA LỖI] Trước đây để null, giờ viết code thật:
    @GetMapping("/{id}")
    public Reservation getReservation(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + id));
    }

    // 3. Check-in (PATCH)
    @PatchMapping("/{id}/check-in")
    public Reservation checkIn(@PathVariable Long id) {
        return reservationService.checkIn(id);
    }

    // 4. Check-out (PATCH)
    @PatchMapping("/{id}/check-out")
    public Reservation checkOut(@PathVariable Long id) {
        return reservationService.checkOut(id);
    }

    // 5. Hủy phòng (PATCH)
    @PatchMapping("/{id}/cancel")
    public Reservation cancel(@PathVariable Long id) {
        return reservationService.cancelReservation(id);
    }

    // 6. Lấy hóa đơn (GET invoice)
    @GetMapping("/{id}/invoice")
    public Invoice getInvoice(@PathVariable Long id) {
        return reservationService.getInvoiceByReservationId(id);
    }

    // 7. Sửa ngày đặt (Modification)
    @PutMapping("/{id}/dates")
    public Reservation updateDates(
            @PathVariable Long id,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut) {
        return reservationService.changeReservationDates(id, checkIn, checkOut);
    }
}