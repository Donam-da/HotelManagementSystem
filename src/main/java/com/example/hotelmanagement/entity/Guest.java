package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;

@Entity
@Table(name = "guests")
@Data
@SQLDelete(sql = "UPDATE guests SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 50, message = "Tên phải từ 2 đến 50 ký tự")
    private String firstName;

    @NotBlank(message = "Họ không được để trống")
    private String lastName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(unique = true)
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