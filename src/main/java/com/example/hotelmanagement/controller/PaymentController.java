package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.PaymentDTO;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Payment", description = "Xử lý thanh toán hóa đơn")
public class PaymentController {

    private final BillingService billingService;

    public PaymentController(BillingService billingService) {
        this.billingService = billingService;
    }

    // POST /api/v1/invoices/{id}/payments
    @PostMapping("/{invoiceId}/payments")
    @Operation(summary = "Thực hiện thanh toán", description = "Ghi nhận giao dịch thanh toán cho một hóa đơn cụ thể.")
    public PaymentDTO processPayment(
            @PathVariable Long invoiceId,
            @RequestBody Payment payment) {
        return billingService.convertToPaymentDTO(billingService.processPayment(invoiceId, payment));
    }

    // POST /api/v1/invoices/{id}/refunds
    @PostMapping("/{invoiceId}/refunds")
    public PaymentDTO processRefund(
            @PathVariable Long invoiceId,
            @RequestParam Double amount,
            @RequestParam String reason) {
        return billingService.convertToPaymentDTO(billingService.processRefund(invoiceId, amount, reason));
    }
}