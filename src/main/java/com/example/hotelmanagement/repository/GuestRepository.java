package com.example.hotelmanagement.repository;
import com.example.hotelmanagement.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    // Tìm khách theo email (để check trùng)
    boolean existsByEmail(String email);
}