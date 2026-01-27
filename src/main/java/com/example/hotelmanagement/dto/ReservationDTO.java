package com.example.hotelmanagement.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationDTO {
    private Long id;
    private String confirmationNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    
    // Chỉ trả về thông tin cần thiết, không trả về cả object Guest/Room
    private String guestName;
    private String roomNumber;
    private Double totalAmount;
}