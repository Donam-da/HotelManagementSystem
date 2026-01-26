package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @Autowired
    private PaymentRepository paymentRepo;

    @GetMapping("/revenue")
    public Map<String, Object> getRevenueReport() {
        // Gọi hàm calculateTotalRevenue() chúng ta đã viết ở bước 3.3
        Double total = paymentRepo.calculateTotalRevenue();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", total == null ? 0.0 : total);
        response.put("generatedDate", java.time.LocalDate.now());
        response.put("status", "SUCCESS");
        
        return response;
    }
}