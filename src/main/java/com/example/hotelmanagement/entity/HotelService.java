package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "services")
public class HotelService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Ví dụ: Coca Cola, Massage, Giặt ủi
    
    // --- BỔ SUNG THEO YÊU CẦU 6.1 ---
    private String description; // Mô tả dịch vụ

    private Double price;       // Giá tiền

    // --- MỚI THÊM: Phân loại dịch vụ (Theo thiết kế 6.1) ---
    private String category;    // Ví dụ: FOOD, BEVERAGE, SPA, LAUNDRY, TRANSPORT

    private Boolean isActive;   // true = Còn phục vụ, false = Ngừng kinh doanh
}