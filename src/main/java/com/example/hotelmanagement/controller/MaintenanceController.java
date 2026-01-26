package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.MaintenanceLog;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.repository.MaintenanceLogRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    @Autowired
    private RoomRepository roomRepository;

    // 1. Báo cáo sự cố mới (Report Issue)
    // POST /api/v1/maintenance/report?roomId=1&description=Hỏng máy lạnh
    @PostMapping("/report")
    public MaintenanceLog reportIssue(
            @RequestParam Long roomId,
            @RequestParam String description) {
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        // Cập nhật trạng thái phòng sang MAINTENANCE để không ai đặt được
        room.setStatus("MAINTENANCE");
        roomRepository.save(room);

        // Tạo log bảo trì
        MaintenanceLog log = new MaintenanceLog();
        log.setRoom(room);
        log.setIssueDescription(description);
        log.setReportedDate(LocalDate.now());
        log.setStatus("PENDING");

        return maintenanceLogRepository.save(log);
    }

    // 2. Hoàn tất bảo trì (Complete Maintenance)
    // PATCH /api/v1/maintenance/{logId}/complete?cost=500000
    @PatchMapping("/{logId}/complete")
    public MaintenanceLog completeMaintenance(
            @PathVariable Long logId,
            @RequestParam Double cost) {
        
        MaintenanceLog log = maintenanceLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu bảo trì"));

        log.setResolvedDate(LocalDate.now());
        log.setCost(cost);
        log.setStatus("COMPLETED");
        
        // Cập nhật lại trạng thái phòng sang AVAILABLE để khách có thể đặt
        Room room = log.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);

        return maintenanceLogRepository.save(log);
    }

    // 3. Xem lịch sử bảo trì của phòng
    @GetMapping("/room/{roomId}")
    public List<MaintenanceLog> getRoomHistory(@PathVariable Long roomId) {
        return maintenanceLogRepository.findByRoomId(roomId);
    }
}
