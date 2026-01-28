package com.example.hotelmanagement.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MaintenanceLogDTO {
    private Long id;
    private String roomNumber;
    private String issueDescription;
    private LocalDate reportedDate;
    private LocalDate resolvedDate;
    private Double cost;
    private String status;
}