package com.example.hotelmanagement.repository;
import com.example.hotelmanagement.entity.HotelService;
import org.springframework.data.jpa.repository.JpaRepository;
public interface HotelServiceRepository extends JpaRepository<HotelService, Long> {}