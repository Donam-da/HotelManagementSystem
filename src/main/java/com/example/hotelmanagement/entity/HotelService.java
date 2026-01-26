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

    private String name;        // Ví dụ: Coca Cola, Massage
    private Double price;       // Giá tiền
    private Boolean isActive;   // Còn phục vụ hay không
}