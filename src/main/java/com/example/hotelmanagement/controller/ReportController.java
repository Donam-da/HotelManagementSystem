package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.repository.GuestRepository;
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

    @Autowired
    private GuestRepository guestRepository;

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
        
        // Đếm số phòng đang có trạng thái 'OCCUPIED'
        long occupiedRooms = roomRepository.findAll().stream()
                .filter(room -> "OCCUPIED".equalsIgnoreCase(room.getStatus()))
                .count();
        
        Map<String, Long> response = new HashMap<>();
        response.put("totalRooms", totalRooms);
        response.put("occupiedRooms", occupiedRooms);
        response.put("availableRooms", totalRooms - occupiedRooms);
        
        return response;
    }

    // 3. Thống kê khách hàng (Guest Statistics)
    @GetMapping("/guest-stats")
    public Map<String, Object> getGuestStatistics() {
        long totalGuests = guestRepository.count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalGuests", totalGuests);
        // Mở rộng: Có thể thêm thống kê khách VIP (điểm > 100), khách mới trong tháng...
        
        return response;
    }
}