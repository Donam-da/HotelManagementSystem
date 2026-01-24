package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.math.BigDecimal; // Dùng BigDecimal cho tiền tệ để chính xác

@Entity
@Data
@Table(name = "room_types")
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ví dụ: Deluxe, Standard
    private BigDecimal basePrice;
    private int maxOccupancy;

    // Quan hệ 1-N: Một loại phòng có nhiều phòng vật lý
    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;
}