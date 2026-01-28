package com.example.hotelmanagement.dto;

import lombok.Data;

@Data
public class RoomDTO {
    private Long id;
    private String roomNumber;
    private String status;
    private String roomTypeName;
    private Double price;
    private Integer capacity;
}