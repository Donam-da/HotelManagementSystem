package com.example.hotelmanagement.service;

import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.InvoiceRepository;
import com.example.hotelmanagement.repository.PaymentRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public ReportService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository, 
                         RoomRepository roomRepository, GuestRepository guestRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
    }

    public Map<String, Object> getRevenueReport() {
        // 5.3 Performance: Dùng Query SUM trực tiếp từ DB thay vì load list Invoice
        Double totalRevenue = paymentRepository.calculateTotalRevenue();

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        response.put("currency", "VNĐ");
        response.put("totalTransactions", invoiceRepository.count()); // Dùng count() thay vì size()
        
        return response;
    }

    public Map<String, Long> getOccupancyReport() {
        long totalRooms = roomRepository.count();
        
        // 5.3 Performance: Dùng countByStatus để DB tự đếm
        long occupiedRooms = roomRepository.countByStatus("OCCUPIED");
        
        Map<String, Long> response = new HashMap<>();
        response.put("totalRooms", totalRooms);
        response.put("occupiedRooms", occupiedRooms);
        response.put("availableRooms", totalRooms - occupiedRooms);
        
        return response;
    }

    public Map<String, Object> getGuestStatistics() {
        long totalGuests = guestRepository.count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalGuests", totalGuests);
        
        return response;
    }
}