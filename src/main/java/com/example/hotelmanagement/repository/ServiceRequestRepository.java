package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // <-- Quan trọng: Nhớ thêm dòng này

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    
    // Tìm tất cả phiếu yêu cầu dịch vụ của một đơn đặt phòng cụ thể
    List<ServiceRequest> findByReservationId(Long reservationId);
    long countByStatus(String status);
}