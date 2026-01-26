package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;          // Số tiền khách trả
    private String paymentMethod;   // CASH, CREDIT_CARD
    private LocalDate paymentDate;

    // --- BỔ SUNG CỘT MỚI (Theo yêu cầu 6.1) ---
    private String transactionId;   // Mã giao dịch (Ví dụ: VCB-123456789)
    // ------------------------------------------

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @JsonIgnore
    private Invoice invoice;        // Trả cho hóa đơn nào
}