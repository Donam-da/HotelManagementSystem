package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String roomNumber; // Ví dụ: 101, 202

    private String status; // Available, Occupied (Có thể nâng cấp thành Enum sau)
    private int floor;

    // Quan hệ N-1: Nhiều phòng thuộc về 1 Loại phòng
    @ManyToOne
    @JoinColumn(name = "room_type_id") // Tên cột khóa ngoại trong DB
    private RoomType roomType;
}