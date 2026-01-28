package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "guests")
@Data
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String address;
    private String idNumber;
    private LocalDate dateOfBirth;
    
    @Column(columnDefinition = "TEXT")
    private String preferences;
    
    private int loyaltyPoints = 0;
    
    private boolean isDeleted = false;
}