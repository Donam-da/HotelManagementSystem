package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.*;
import com.example.hotelmanagement.service.BillingService;
import com.example.hotelmanagement.service.HotelServiceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final HotelServiceService hotelServiceService;
    private final BillingService billingService;

    public ServiceController(HotelServiceService hotelServiceService, BillingService billingService) {
        this.hotelServiceService = hotelServiceService;
        this.billingService = billingService;
    }

    // 1. Xem danh sách dịch vụ
    @GetMapping
    public List<HotelService> getAllServices() {
        return hotelServiceService.getAllServices();
    }

    // 2. Tạo dịch vụ mới (cho Admin)
    @PostMapping
    public HotelService createService(@RequestBody HotelService service) {
        return hotelServiceService.createService(service);
    }

    // 3. Khách gọi dịch vụ
    @PostMapping("/request")
    public ServiceRequest requestService(@RequestParam Long reservationId, 
                                         @RequestParam Long serviceId, 
                                         @RequestParam Integer quantity) {
        // Sử dụng BillingService để đảm bảo tính tiền vào hóa đơn (Fix lỗi DRY & Logic)
        return billingService.addServiceRequest(reservationId, serviceId, quantity);
    }
}