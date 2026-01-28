package com.example.hotelmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationRequestDTO {
    @NotNull(message = "Ngày Check-in không được để trống")
    private LocalDate checkInDate;
    @NotNull(message = "Ngày Check-out không được để trống")
    private LocalDate checkOutDate;
    private Long guestId;
    private Long roomId;
}