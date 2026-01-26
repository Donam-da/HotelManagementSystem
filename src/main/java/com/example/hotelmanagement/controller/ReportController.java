package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.repository.InvoiceRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// --- CÁC DÒNG IMPORT QUAN TRỌNG THƯỜNG BỊ THIẾU ---
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.example.hotelmanagement.entity.Invoice; 
// --------------------------------------------------

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private RoomRepository roomRepository;

    // 1. Thống kê doanh thu (Revenue Analytics)
    @GetMapping("/revenue")
    public Map<String, Object> getRevenueReport() {
        List<Invoice> invoices = invoiceRepository.findAll();
        
        // Tính tổng tiền (Nếu null thì tính là 0)
        Double totalRevenue = invoices.stream()
                .mapToDouble(inv -> inv.getTotalAmount() != null ? inv.getTotalAmount() : 0.0)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue);
        response.put("currency", "VNĐ");
        response.put("totalTransactions", invoices.size());
        
        return response;
    }

    // 2. Thống kê tình trạng phòng (Occupancy Report)
    @GetMapping("/occupancy")
    public Map<String, Long> getOccupancyReport() {
        long totalRooms = roomRepository.count();
        
        // Đếm số phòng đang có trạng thái 'OCCUPIED' (Nếu bạn có quản lý status)
        // Hiện tại ta chỉ báo cáo tổng số phòng để demo
        
        Map<String, Long> response = new HashMap<>();
        response.put("totalRooms", totalRooms);
        // response.put("occupiedRooms", ...); // Phát triển sau
        
        return response;
    }
}