package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String confirmationNumber; // Mã đặt phòng

    @NotNull(message = "Ngày Check-in không được để trống")
    private LocalDate checkInDate;
    @NotNull(message = "Ngày Check-out không được để trống")
    private LocalDate checkOutDate;
    private String status;
    
    // --- BỔ SUNG ĐỂ LƯU PHÍ PHẠT HỦY (BR-005, BR-006) ---
    private Double cancellationFee = 0.0;

    // --- QUAN HỆ VỚI GUEST ---
    @ManyToOne(fetch = FetchType.LAZY) // Tối ưu Performance (5.3)
    @JoinColumn(name = "guest_id")
    @JsonIgnore
    private Guest guest;

    // --- QUAN HỆ VỚI ROOM (QUAN TRỌNG: Cần cái này để hết lỗi setRoom) ---
    @ManyToOne(fetch = FetchType.LAZY) // Tối ưu Performance (5.3)
    @JoinColumn(name = "room_id")
    private Room room;
    
    // --- QUAN HỆ VỚI INVOICE ---
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Invoice invoice;

    // --- BỔ SUNG THEO YÊU CẦU 6.1 (Quan hệ với ReservationRoom) ---
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<ReservationRoom> reservationRooms; // Danh sách phòng trong đơn đặt

    // Tự động sinh mã khi tạo mới
    @PrePersist
    protected void onCreate() {
        if (this.confirmationNumber == null) {
            // BR-008: Format HTLYYYYMMDD-XXXXX
            String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String randomPart = java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            this.confirmationNumber = "HTL" + datePart + "-" + randomPart;
        }
    }
}