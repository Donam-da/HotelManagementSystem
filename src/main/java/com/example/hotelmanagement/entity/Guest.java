package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "guests")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;     // Đã thêm lúc nãy
    
    // --- BẠN HÃY THÊM DÒNG NÀY VÀO ---
    private int loyaltyPoints; 
    // ---------------------------------

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String idNumber;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}