package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "amenities")
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;       // Ví dụ: Wifi, Tivi, Tủ lạnh
    private String description; // Mô tả chi tiết

    // --- BỔ SUNG CỘT MỚI THEO YÊU CẦU 6.1 ---
    // Lưu tên file icon hoặc mã icon (ví dụ: "wifi.png" hoặc "fa-solid fa-wifi")
    private String icon;       
    // ----------------------------------------

    // Quan hệ nhiều-nhiều với Loại phòng (Một tiện nghi có ở nhiều loại phòng)
    @ManyToMany(mappedBy = "amenities")
    @JsonIgnore // Ngắt vòng lặp JSON để tránh lỗi Infinite Recursion
    private List<RoomType> roomTypes;
}