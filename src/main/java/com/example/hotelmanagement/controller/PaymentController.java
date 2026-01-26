package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.exception.ResourceNotFoundException;
import com.example.hotelmanagement.repository.InvoiceRepository;
import com.example.hotelmanagement.repository.PaymentRepository;
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
}