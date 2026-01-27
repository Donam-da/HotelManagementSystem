package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
public class PaymentController {

    @Autowired
    private BillingService billingService;

    // POST /api/v1/invoices/{id}/payments
    @PostMapping("/{invoiceId}/payments")
    public Payment processPayment(
            @PathVariable Long invoiceId,
            @RequestBody Payment payment) {
        return billingService.processPayment(invoiceId, payment);
    }

    // POST /api/v1/invoices/{id}/refunds
    @PostMapping("/{invoiceId}/refunds")
    public Payment processRefund(
            @PathVariable Long invoiceId,
            @RequestParam Double amount,
            @RequestParam String reason) {
        return billingService.processRefund(invoiceId, amount, reason);
    }
}