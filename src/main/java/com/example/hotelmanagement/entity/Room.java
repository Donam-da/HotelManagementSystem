package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_number", columnList = "roomNumber")
})
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Số phòng không được để trống")
    @Column(unique = true, nullable = false)
    private String roomNumber; // Ví dụ: 101, 202

    @Enumerated(EnumType.STRING)
    private RoomStatus status; 
    
    private int floor; // Tầng

    // --- CÁC CỘT MỚI BỔ SUNG (Theo yêu cầu mục 6.1) ---
    @Column(name = "view_type")
    private String viewType;     // Ví dụ: Sea View, City View, Garden View
    
    @Column(name = "is_smoking")
    private Boolean isSmoking;   // true = Cho phép hút thuốc, false = Phòng cấm hút thuốc

    // Quan hệ N-1: Nhiều phòng thuộc về 1 Loại phòng
    @ManyToOne
    @JoinColumn(name = "room_type_id") // Tên cột khóa ngoại trong DB
    private RoomType roomType;
}