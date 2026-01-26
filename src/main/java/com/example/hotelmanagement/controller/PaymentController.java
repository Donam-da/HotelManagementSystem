package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.exception.ResourceNotFoundException;
import com.example.hotelmanagement.repository.InvoiceRepository;
import com.example.hotelmanagement.repository.PaymentRepository;
import com.example.hotelmanagement.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // <--- Quan trọng: Import LocalDateTime

@RestController
@RequestMapping("/api/v1/invoices")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private BillingService billingService;

    // POST /api/v1/invoices/{id}/payments
    @PostMapping("/{invoiceId}/payments")
    public Payment processPayment(
            @PathVariable Long invoiceId,
            @RequestBody Payment payment) {
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + invoiceId));

        payment.setInvoice(invoice);
        
        // SỬA: Lưu thời gian thực (Ngày + Giờ)
        payment.setPaymentDate(LocalDateTime.now()); 
        
        return paymentRepository.save(payment);
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