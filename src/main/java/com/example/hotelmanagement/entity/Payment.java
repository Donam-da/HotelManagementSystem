package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime; // <-- QUAN TRỌNG: Dùng cái này để lưu cả giờ
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;          // Số tiền khách trả
    private String paymentMethod;   // CASH, CREDIT_CARD, BANK_TRANSFER

    // SỬA ĐỔI: Dùng LocalDateTime để lưu chính xác thời gian (ngày + giờ)
    private LocalDateTime paymentDate;

    // --- BỔ SUNG CỘT MỚI (Theo yêu cầu 6.1) ---
    private String transactionId;   // Mã giao dịch (Ví dụ: VCB-123456789)
    // ------------------------------------------

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @JsonIgnore // Ngắt vòng lặp vô hạn khi trả về JSON qua API
    private Invoice invoice;        // Trả cho hóa đơn nào
}