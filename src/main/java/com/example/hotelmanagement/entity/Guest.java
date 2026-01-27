package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "guests", indexes = {
    @Index(name = "idx_guest_email", columnList = "email"),
    @Index(name = "idx_guest_phone", columnList = "phone")
})
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    // --- BỔ SUNG THEO YÊU CẦU 8.2 (BR-101) ---
    private LocalDate dateOfBirth;

    // --- CÁC TRƯỜNG BỔ SUNG THEO YÊU CẦU 6.1 ---
    private String address;      // Địa chỉ
    private String idNumber;     // Số CCCD / Hộ chiếu
    
    // --- BỔ SUNG THEO YÊU CẦU 4.2.1 (Track preferences) ---
    private String preferences;  // Sở thích: Tầng cao, yên tĩnh, dị ứng...

    // Chỉ giữ lại 1 dòng này thôi:
    private Integer loyaltyPoints = 0; // Điểm thành viên (Mặc định là 0)

    // --- BỔ SUNG CHO YÊU CẦU 7.2 (Soft Delete) ---
    private boolean isDeleted = false; // false = Hoạt động, true = Đã xóa

    // --- QUAN HỆ ---
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
    @JsonIgnore // Ngăn chặn lỗi vòng lặp vô hạn khi gọi API
    private List<Reservation> reservations;
}