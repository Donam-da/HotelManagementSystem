package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 1. Thống kê doanh thu (Revenue Analytics)
    @GetMapping("/revenue")
    public Map<String, Object> getRevenueReport() {
        return reportService.getRevenueReport();
    }

    // 2. Thống kê tình trạng phòng (Occupancy Report)
    @GetMapping("/occupancy")
    public Map<String, Long> getOccupancyReport() {
        return reportService.getOccupancyReport();
    }

    // 3. Thống kê khách hàng (Guest Statistics)
    @GetMapping("/guest-stats")
    public Map<String, Object> getGuestStatistics() {
        return reportService.getGuestStatistics();
    }

    // 4. Thống kê trạng thái phòng (Cho biểu đồ tròn)
    @GetMapping("/room-status")
    public Map<String, Long> getRoomStatusStats() {
        return reportService.getRoomStatusStats();
    }

    // 5. Thông báo nhanh (Notifications)
    @GetMapping("/notifications")
    public List<Map<String, String>> getNotifications() {
        return reportService.getDashboardNotifications();
    }

    // 6. Biểu đồ công suất phòng (Occupancy History)
    @GetMapping("/occupancy-history")
    public Map<String, Object> getOccupancyHistory() {
        return reportService.getOccupancyHistory();
    }

    // 7. Top khách hàng thân thiết
    @GetMapping("/top-guests")
    public List<Guest> getTopGuests() {
        return reportService.getTopLoyalGuests();
    }
}