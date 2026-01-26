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
    
 // Trong file entity/Reservation.java
 // 1. Thêm trường này
    @Column(unique = true)
    private String confirmationNumber;

 // 2. Thêm hàm này (Tự chạy trước khi lưu vào DB)
    @PrePersist
 	protected void onCreate() {
     if (this.confirmationNumber == null) {
         // Tạo mã kiểu: RES + Thời gian + 4 ký tự ngẫu nhiên
         String timePart = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(java.time.LocalDate.now());
         String randomPart = java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase();
         this.confirmationNumber = "RES-" + timePart + "-" + randomPart;
     }
    }
}