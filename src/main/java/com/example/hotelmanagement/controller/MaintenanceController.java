package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.MaintenanceLog;
import com.example.hotelmanagement.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    // 1. Báo cáo sự cố mới (Report Issue)
    // POST /api/v1/maintenance/report?roomId=1&description=Hỏng máy lạnh
    @PostMapping("/report")
    public MaintenanceLog reportIssue(
            @RequestParam Long roomId,
            @RequestParam String description) {
        return maintenanceService.reportIssue(roomId, description);
    }

    // 2. Hoàn tất bảo trì (Complete Maintenance)
    // PATCH /api/v1/maintenance/{logId}/complete?cost=500000
    @PatchMapping("/{logId}/complete")
    public MaintenanceLog completeMaintenance(
            @PathVariable Long logId,
            @RequestParam Double cost) {
        return maintenanceService.completeMaintenance(logId, cost);
    }

    // 3. Xem lịch sử bảo trì của phòng
    @GetMapping("/room/{roomId}")
    public List<MaintenanceLog> getRoomHistory(@PathVariable Long roomId) {
        return maintenanceService.getRoomHistory(roomId);
    }
}
