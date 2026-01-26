package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Long> {
    List<MaintenanceLog> findByRoomId(Long roomId); // Xem lịch sử của 1 phòng
    List<MaintenanceLog> findByStatus(String status); // Xem các đơn chưa xử lý
}
