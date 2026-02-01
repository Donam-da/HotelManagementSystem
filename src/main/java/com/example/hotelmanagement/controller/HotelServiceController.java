package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.HotelService;
import com.example.hotelmanagement.service.HotelServiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Hotel Services", description = "Quản lý dịch vụ khách sạn (Spa, Ăn uống...)")
public class HotelServiceController {

    private final HotelServiceService serviceService;

    public HotelServiceController(HotelServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public List<HotelService> getAllServices() {
        return serviceService.getAllServices();
    }

    @PostMapping
    public HotelService createService(@RequestBody HotelService service) {
        return serviceService.createService(service);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}