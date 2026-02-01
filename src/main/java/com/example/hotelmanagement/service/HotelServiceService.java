package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.HotelService;
import com.example.hotelmanagement.repository.HotelServiceRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

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
        return repository.save(Objects.requireNonNull(service));
    }

    public void deleteService(Long id) {
        repository.deleteById(Objects.requireNonNull(id));
    }
}