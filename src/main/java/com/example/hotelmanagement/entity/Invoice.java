package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;
    private BigDecimal taxAmount;

    // Quan hệ 1-1: Khóa ngoại trỏ về Reservation
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;


    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private java.util.List<Payment> payments;
}