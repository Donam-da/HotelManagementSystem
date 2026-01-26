package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Query đặc biệt cho phần Báo cáo (Mục 4)
    @Query("SELECT SUM(p.amount) FROM Payment p")
    Double calculateTotalRevenue();
}