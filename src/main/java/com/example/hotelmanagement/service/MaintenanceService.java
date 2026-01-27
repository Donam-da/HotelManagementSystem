package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.MaintenanceLog;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.repository.MaintenanceLogRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired private MaintenanceLogRepository maintenanceLogRepository;
    @Autowired private RoomRepository roomRepository;

    public MaintenanceLog reportIssue(Long roomId, String description) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        // Cập nhật trạng thái phòng
        room.setStatus("MAINTENANCE");
        roomRepository.save(room);

        MaintenanceLog log = new MaintenanceLog();
        log.setRoom(room);
        log.setIssueDescription(description);
        log.setReportedDate(LocalDate.now());
        log.setStatus("PENDING");

        return maintenanceLogRepository.save(log);
    }

    public MaintenanceLog completeMaintenance(Long logId, Double cost) {
        MaintenanceLog log = maintenanceLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu bảo trì"));

        log.setResolvedDate(LocalDate.now());
        log.setCost(cost);
        log.setStatus("COMPLETED");
        
        Room room = log.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);

        return maintenanceLogRepository.save(log);
    }

    public List<MaintenanceLog> getRoomHistory(Long roomId) {
        return maintenanceLogRepository.findByRoomId(roomId);
    }
}