package com.example.hotelmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class GuestDTO {
    private Long id;
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 50, message = "Tên phải từ 2 đến 50 ký tự")
    private String firstName;
    @NotBlank(message = "Họ không được để trống")
    private String lastName;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
    private String phone;
    private String address;
    private String idNumber;
    private LocalDate dateOfBirth;
    private String preferences;
    private int loyaltyPoints;
}