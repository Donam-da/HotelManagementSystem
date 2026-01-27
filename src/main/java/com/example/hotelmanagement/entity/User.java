package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // --- BỔ SUNG ĐỊNH DANH CHO NHÂN VIÊN (STAFF) ---
    private String fullName;     // Tên thật của nhân viên (VD: Nguyễn Văn A)
    private String email;        // Email nội bộ hoặc liên hệ

    // Một User có thể có nhiều Role (Ví dụ: Manager cũng có thể làm quyền Receptionist)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    // Nếu User là Guest, có thể link tới bảng Guest (Optional)
    @OneToOne
    @JoinColumn(name = "guest_profile_id")
    private Guest guestProfile;
}