package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.HotelService;
import com.example.hotelmanagement.repository.HotelServiceRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelServiceService {
    private final HotelServiceRepository repository;

    public HotelServiceService(HotelServiceRepository repository) {
        this.repository = repository;
    }

    public List<HotelService> getAllServices() {
        return repository.findAll();
    }

    public HotelService createService(HotelService service) {
        return repository.save(service);
    }
}