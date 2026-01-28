package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- CÁC CỘT MỚI THÊM (Theo yêu cầu 6.1) ---
    @Column(unique = true)
    private String invoiceNumber; // Mã hóa đơn (Ví dụ: INV-2026-001)

    private Double subtotal;      // Tổng tiền trước thuế
    private Double serviceFee;    // Phí dịch vụ (5%) - BỔ SUNG THEO YÊU CẦU 4.2.4
    private Double taxAmount;     // Tiền thuế (10%)
    private Double discountAmount = 0.0; // Giảm giá (Loyalty Points) - BR-103
    private Double totalAmount;   // Tổng cộng thanh toán (Sau thuế)

    // --- QUAN HỆ ---
    
    // Quan hệ 1-1: Khóa ngoại trỏ về Reservation
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    @JsonIgnore // Tránh vòng lặp vô hạn khi chuyển thành JSON
    private Reservation reservation;

    // Một hóa đơn có thể được thanh toán làm nhiều lần (Cọc trước, trả sau...)
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Payment> payments;

    // --- LOGIC TỰ ĐỘNG SINH MÃ HÓA ĐƠN ---
    @PrePersist
    protected void onCreate() {
        if (this.invoiceNumber == null) {
            // Tạo mã kiểu: INV + Ngày tháng + 4 ký tự ngẫu nhiên
            // BR-303: Format INV-YYYYMMDD-XXXXX (5 ký tự random)
            String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            this.invoiceNumber = "INV-" + datePart + "-" + randomPart;
        }
    }
}