package com.example.hotelmanagement.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String transactionId;
    private Long invoiceId;
}