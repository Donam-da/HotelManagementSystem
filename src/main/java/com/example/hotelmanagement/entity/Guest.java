package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Data
@Table(name = "guests")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    // --- CÁC TRƯỜNG BỔ SUNG THEO YÊU CẦU 6.1 ---
    private String address;      // Địa chỉ
    private String idNumber;     // Số CCCD / Hộ chiếu
    
    // Chỉ giữ lại 1 dòng này thôi:
    private Integer loyaltyPoints = 0; // Điểm thành viên (Mặc định là 0)

    // --- QUAN HỆ ---
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
    @JsonIgnore // Ngăn chặn lỗi vòng lặp vô hạn khi gọi API
    private List<Reservation> reservations;
}