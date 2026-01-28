package com.example.hotelmanagement.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GuestDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String idNumber;
    private LocalDate dateOfBirth;
    private String preferences;
    private int loyaltyPoints;
}