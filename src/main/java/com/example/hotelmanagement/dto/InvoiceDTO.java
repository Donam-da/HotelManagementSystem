package com.example.hotelmanagement.dto;

import lombok.Data;

@Data
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private Double subtotal;
    private Double serviceFee;
    private Double taxAmount;
    private Double discountAmount;
    private Double totalAmount;
    private Long reservationId;
}