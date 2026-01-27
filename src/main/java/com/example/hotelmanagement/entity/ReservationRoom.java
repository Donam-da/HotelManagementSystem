package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "reservation_rooms")
public class ReservationRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double pricePerNight; // Giá phòng 1 đêm (Snapshot giá tại thời điểm đặt)
    
    private Double totalPrice;    // Tổng tiền = pricePerNight * số đêm

    // --- QUAN HỆ ---
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}