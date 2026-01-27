package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "service_requests")
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation; // Dịch vụ này tính cho đơn đặt phòng nào

    @ManyToOne
    @JoinColumn(name = "service_id")
    private HotelService hotelService; // Khách gọi món gì

    private Integer quantity; // Số lượng
    private Double totalCost; // Tổng tiền = giá * số lượng
    private LocalDateTime requestDate;

    // --- BỔ SUNG THEO YÊU CẦU 4.2.4 (Track status) ---
    private String status = "PENDING";    // Mặc định là PENDING khi tạo mới

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}