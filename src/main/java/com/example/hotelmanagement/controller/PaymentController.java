package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.*;
import com.example.hotelmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired private PaymentRepository paymentRepo;
    @Autowired private InvoiceRepository invoiceRepo;

    // API thực hiện thanh toán
    @PostMapping
    public Payment makePayment(@RequestParam Long invoiceId, 
                               @RequestBody Payment payment) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
             .orElseThrow(() -> new RuntimeException("Invoice not found"));
             
        payment.setInvoice(invoice);
        payment.setPaymentDate(LocalDate.now());
        return paymentRepo.save(payment);
    }
}