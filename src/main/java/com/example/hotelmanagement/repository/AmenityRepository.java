package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    // Có thể thêm: boolean existsByName(String name);
}