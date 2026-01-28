package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.ReservationDTO;
import com.example.hotelmanagement.dto.InvoiceDTO;
import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.service.ReservationService;
import com.example.hotelmanagement.service.BillingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate; // Import LocalDate

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final BillingService billingService;

    public ReservationController(ReservationService reservationService, BillingService billingService) {
        this.reservationService = reservationService;
        this.billingService = billingService;
    }

    // Helper: Chuyển đổi Entity sang DTO (Yêu cầu 5.3 - Use DTOs)
    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setConfirmationNumber(reservation.getConfirmationNumber());
        dto.setCheckInDate(reservation.getCheckInDate());
        dto.setCheckOutDate(reservation.getCheckOutDate());
        dto.setStatus(reservation.getStatus());
        
        if (reservation.getGuest() != null) {
            dto.setGuestName(reservation.getGuest().getFirstName() + " " + reservation.getGuest().getLastName());
        }
        
        if (reservation.getRoom() != null) {
            dto.setRoomNumber(reservation.getRoom().getRoomNumber());
        }
        
        if (reservation.getInvoice() != null) {
            dto.setTotalAmount(reservation.getInvoice().getTotalAmount());
        }
        
        return dto;
    }

    // 1. Tạo đơn đặt phòng (POST)
    @PostMapping
    public ReservationDTO createReservation(
            @RequestBody Reservation reservation,
            @RequestParam Long guestId,
            @RequestParam Long roomId) {
        return convertToDTO(reservationService.createReservation(reservation, guestId, roomId));
    }

    // 2. Lấy chi tiết đơn (GET /api/v1/reservations/{id})
    @GetMapping("/{id}")
    public ReservationDTO getReservation(@PathVariable Long id) {
        // 5.3 Architecture: Gọi qua Service, không gọi Repository trực tiếp
        return convertToDTO(reservationService.getReservationById(id));
    }

    // 2b. Lấy danh sách có phân trang (GET /api/v1/reservations?page=0&size=10)
    // (Yêu cầu 5.2 - Pagination)
    @GetMapping
    public Page<ReservationDTO> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reservationService.getAllReservations(pageable).map(this::convertToDTO);
    }

    // 3. Check-in (PATCH)
    @PatchMapping("/{id}/check-in")
    public ReservationDTO checkIn(@PathVariable Long id) {
        return convertToDTO(reservationService.checkIn(id));
    }

    // 4. Check-out (PATCH)
    @PatchMapping("/{id}/check-out")
    public ReservationDTO checkOut(@PathVariable Long id) {
        return convertToDTO(reservationService.checkOut(id));
    }

    // 5. Hủy phòng (PATCH)
    @PatchMapping("/{id}/cancel")
    public ReservationDTO cancel(@PathVariable Long id) {
        return convertToDTO(reservationService.cancelReservation(id));
    }

    // 6. Lấy hóa đơn (GET invoice)
    @GetMapping("/{id}/invoice")
    public InvoiceDTO getInvoice(@PathVariable Long id) {
        Invoice invoice = reservationService.getInvoiceByReservationId(id);
        return billingService.convertToInvoiceDTO(invoice);
    }

    // 7. Sửa đơn đặt phòng (Modification - UC-004)
    // Hỗ trợ đổi ngày, đổi phòng, đổi khách
    @PutMapping("/{id}")
    public ReservationDTO modifyReservation(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate checkIn,
            @RequestParam(required = false) LocalDate checkOut,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Long guestId) {
        return convertToDTO(reservationService.modifyReservation(id, checkIn, checkOut, roomId, guestId));
    }

    // 8. Báo khách không đến (No-Show - UC-005)
    @PatchMapping("/{id}/no-show")
    public ReservationDTO markAsNoShow(@PathVariable Long id) {
        return convertToDTO(reservationService.markAsNoShow(id));
    }
}