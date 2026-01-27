package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "maintenance_logs")
public class MaintenanceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private String issueDescription; // Mô tả sự cố: "Hỏng điều hòa", "Vòi nước rò rỉ"

    private LocalDate reportedDate;  // Ngày báo cáo
    private LocalDate resolvedDate;  // Ngày sửa xong

    private Double cost;             // Chi phí sửa chữa
    
    private String status;           // PENDING, IN_PROGRESS, COMPLETED

    // --- BỔ SUNG LIÊN KẾT VỚI NHÂN VIÊN (Housekeeping Staff) ---
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User assignedStaff;      // Nhân viên thực hiện bảo trì
}
