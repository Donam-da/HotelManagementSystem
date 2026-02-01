package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;      // Tên vật tư (Nước suối, Bàn chải...)
    private Integer quantity; // Số lượng tồn kho
    private String unit;      // Đơn vị tính (Chai, Cái, Hộp)
    private String category;  // Phân loại (Minibar, Bathroom, Bedding)
}