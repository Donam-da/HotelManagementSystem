package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.*;
import com.example.hotelmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    @Autowired private HotelServiceRepository serviceRepo;
    @Autowired private ServiceRequestRepository requestRepo;
    @Autowired private ReservationRepository reservationRepo;

    // 1. Xem danh sách dịch vụ
    @GetMapping
    public List<HotelService> getAllServices() {
        return serviceRepo.findAll();
    }

    // 2. Tạo dịch vụ mới (cho Admin)
    @PostMapping
    public HotelService createService(@RequestBody HotelService service) {
        return serviceRepo.save(service);
    }

    // 3. Khách gọi dịch vụ
    @PostMapping("/request")
    public ServiceRequest requestService(@RequestParam Long reservationId, 
                                         @RequestParam Long serviceId, 
                                         @RequestParam Integer quantity) {
        // Tìm đơn đặt phòng và dịch vụ
        Reservation res = reservationRepo.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt phòng"));
        HotelService srv = serviceRepo.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ"));

        // Tạo phiếu yêu cầu
        ServiceRequest req = new ServiceRequest();
        req.setReservation(res);
        req.setHotelService(srv);
        req.setQuantity(quantity);
        req.setTotalCost(srv.getPrice() * quantity);
        req.setRequestDate(LocalDateTime.now());

        return requestRepo.save(req);
    }
}