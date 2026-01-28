package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.MaintenanceLogDTO;
import com.example.hotelmanagement.service.MaintenanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    // 1. Báo cáo sự cố mới (Report Issue)
    // POST /api/v1/maintenance/report?roomId=1&description=Hỏng máy lạnh
    @PostMapping("/report")
    public MaintenanceLogDTO reportIssue(
            @RequestParam Long roomId,
            @RequestParam String description) {
        return maintenanceService.convertToDTO(maintenanceService.reportIssue(roomId, description));
    }

    // 2. Hoàn tất bảo trì (Complete Maintenance)
    // PATCH /api/v1/maintenance/{logId}/complete?cost=500000
    @PatchMapping("/{logId}/complete")
    public MaintenanceLogDTO completeMaintenance(
            @PathVariable Long logId,
            @RequestParam Double cost) {
        return maintenanceService.convertToDTO(maintenanceService.completeMaintenance(logId, cost));
    }

    // 3. Xem lịch sử bảo trì của phòng
    @GetMapping("/room/{roomId}")
    public List<MaintenanceLogDTO> getRoomHistory(@PathVariable Long roomId) {
        return maintenanceService.getRoomHistory(roomId).stream()
                .map(maintenanceService::convertToDTO)
                .collect(Collectors.toList());
    }
}
