package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.ReservationDTO;
import com.example.hotelmanagement.dto.ReservationRequestDTO;
import com.example.hotelmanagement.dto.InvoiceDTO;
import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.service.ReservationService;
import com.example.hotelmanagement.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDate; // Import LocalDate

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "Các API quản lý quy trình đặt phòng, Check-in/out")
public class ReservationController {

    private final ReservationService reservationService;
    private final BillingService billingService;

    public ReservationController(ReservationService reservationService, BillingService billingService) {
        this.reservationService = reservationService;
        this.billingService = billingService;
    }

    // 1. Tạo đơn đặt phòng (POST)
    @Operation(summary = "Tạo mới đơn đặt phòng", description = "Tạo một đơn đặt phòng mới cho khách hàng và phòng cụ thể. Hệ thống sẽ tự động kiểm tra phòng trống.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công", content = @Content(schema = @Schema(implementation = ReservationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc phòng đã bị trùng lịch"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy Guest hoặc Room")
    })
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO request) {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        
        Reservation savedReservation = reservationService.createReservation(reservation, request.getGuestId(), request.getRoomId());
        ReservationDTO dto = reservationService.convertToDTO(savedReservation);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(dto);
    }

    // 2. Lấy chi tiết đơn (GET /api/v1/reservations/{id})
    @Operation(summary = "Lấy chi tiết đơn đặt phòng", description = "Trả về thông tin chi tiết của một đơn đặt phòng dựa trên ID.")
    @GetMapping("/{id}")
    public ReservationDTO getReservation(@PathVariable Long id) {
        // 5.3 Architecture: Gọi qua Service, không gọi Repository trực tiếp
        return reservationService.convertToDTO(reservationService.getReservationById(id));
    }

    // 2b. Lấy danh sách có phân trang (GET /api/v1/reservations?page=0&size=10)
    // (Yêu cầu 5.2 - Pagination)
    @Operation(summary = "Lấy danh sách đơn đặt phòng", description = "Hỗ trợ phân trang để tối ưu hiệu năng.")
    @GetMapping
    public Page<ReservationDTO> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reservationService.getAllReservations(pageable).map(reservationService::convertToDTO);
    }

    // 3. Check-in (PATCH)
    @Operation(summary = "Thực hiện Check-in", description = "Chuyển trạng thái đơn từ CONFIRMED sang CHECKED_IN và cập nhật trạng thái phòng.")
    @PatchMapping("/{id}/check-in")
    public ReservationDTO checkIn(@PathVariable Long id) {
        return reservationService.convertToDTO(reservationService.checkIn(id));
    }

    // 4. Check-out (PATCH)
    @Operation(summary = "Thực hiện Check-out", description = "Chuyển trạng thái sang CHECKED_OUT, giải phóng phòng và tính toán hóa đơn cuối cùng.")
    @PatchMapping("/{id}/check-out")
    public ReservationDTO checkOut(@PathVariable Long id) {
        return reservationService.convertToDTO(reservationService.checkOut(id));
    }

    // 5. Hủy phòng (PATCH)
    @Operation(summary = "Hủy đặt phòng", description = "Hủy đơn đặt phòng và tính phí phạt dựa trên thời gian hủy.")
    @PatchMapping("/{id}/cancel")
    public ReservationDTO cancel(@PathVariable Long id) {
        return reservationService.convertToDTO(reservationService.cancelReservation(id));
    }

    // 6. Lấy hóa đơn (GET invoice)
    @Operation(summary = "Lấy hóa đơn của đơn đặt phòng", description = "Trả về thông tin hóa đơn bao gồm tiền phòng, dịch vụ, thuế và phí.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Đơn đặt phòng không tồn tại")
    })
    @GetMapping("/{id}/invoice")
    public InvoiceDTO getInvoice(@PathVariable Long id) {
        Invoice invoice = reservationService.getInvoiceByReservationId(id);
        return billingService.convertToInvoiceDTO(invoice);
    }

    // 7. Sửa đơn đặt phòng (Modification - UC-004)
    // Hỗ trợ đổi ngày, đổi phòng, đổi khách
    @Operation(summary = "Cập nhật thông tin đặt phòng", description = "Cho phép thay đổi ngày lưu trú, phòng hoặc khách hàng khi đơn ở trạng thái PENDING/CONFIRMED.")
    @PutMapping("/{id}")
    public ReservationDTO modifyReservation(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate checkIn,
            @RequestParam(required = false) LocalDate checkOut,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Long guestId) {
        return reservationService.convertToDTO(reservationService.modifyReservation(id, checkIn, checkOut, roomId, guestId));
    }

    // 8. Báo khách không đến (No-Show - UC-005)
    @Operation(summary = "Đánh dấu khách không đến (No-Show)", description = "Sử dụng khi khách không đến nhận phòng đúng hạn.")
    @PatchMapping("/{id}/no-show")
    public ReservationDTO markAsNoShow(@PathVariable Long id) {
        return reservationService.convertToDTO(reservationService.markAsNoShow(id));
    }
}