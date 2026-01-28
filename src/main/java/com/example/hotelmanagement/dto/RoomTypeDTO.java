package com.example.hotelmanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoomTypeDTO {
    private Long id;
    private String name;
    private String description;
    private Double basePrice;
    private int maxOccupancy;
    private String bedType;
    private List<AmenityDTO> amenities;
}