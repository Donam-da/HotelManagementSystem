package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore; // <--- 1. Thêm dòng này

@Entity
@Data
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    @JsonIgnore  // <--- 2. Thêm dòng này (Để ngắt vòng lặp)
    private Guest guest;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Invoice invoice;
}