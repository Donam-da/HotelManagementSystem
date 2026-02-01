package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.entity.RoomStatus;
import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.InvoiceRepository;
import com.example.hotelmanagement.repository.PaymentRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import com.example.hotelmanagement.repository.ServiceRequestRepository;
import com.example.hotelmanagement.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ReservationRepository reservationRepository;

    public ReportService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository, 
                         RoomRepository roomRepository, GuestRepository guestRepository,
                         ServiceRequestRepository serviceRequestRepository,
                         ReservationRepository reservationRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.reservationRepository = reservationRepository;
    }

    public Map<String, Object> getRevenueReport() {
        // 5.3 Performance: Dùng Query SUM trực tiếp từ DB thay vì load list Invoice
        Double totalRevenue = paymentRepository.calculateTotalRevenue();

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        response.put("currency", "VNĐ");
        response.put("totalTransactions", invoiceRepository.count()); // Dùng count() thay vì size()
        
        // Mockup data cho biểu đồ (Vì chưa có query phức tạp theo ngày)
        response.put("thisWeekData", new long[]{1500000, 2300000, 1800000, 3200000, 2900000, 4500000, 5100000});
        response.put("lastWeekData", new long[]{1200000, 2100000, 1600000, 2800000, 2500000, 4100000, 4800000});
        
        return response;
    }

    public Map<String, Long> getOccupancyReport() {
        long totalRooms = roomRepository.count();
        
        // 5.3 Performance: Dùng countByStatus để DB tự đếm
        long occupiedRooms = roomRepository.countByStatus(RoomStatus.OCCUPIED);
        
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

    // --- BỔ SUNG CHO DASHBOARD MỚI ---
    public Map<String, Long> getRoomStatusStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("AVAILABLE", roomRepository.countByStatus(RoomStatus.AVAILABLE));
        stats.put("OCCUPIED", roomRepository.countByStatus(RoomStatus.OCCUPIED));
        stats.put("MAINTENANCE", roomRepository.countByStatus(RoomStatus.MAINTENANCE));
        // Giả sử có trạng thái DIRTY, nếu chưa có enum thì trả về 0 hoặc thêm vào enum
        try {
            stats.put("DIRTY", roomRepository.countByStatus(RoomStatus.valueOf("DIRTY")));
        } catch (IllegalArgumentException e) {
            stats.put("DIRTY", 0L);
        }
        return stats;
    }

    public List<Map<String, String>> getDashboardNotifications() {
        List<Map<String, String>> notis = new ArrayList<>();

        // 1. Yêu cầu dịch vụ chưa xử lý
        long pendingRequests = serviceRequestRepository.countByStatus("PENDING");
        if (pendingRequests > 0) {
            notis.add(Map.of("type", "SERVICE", "title", "Yêu cầu dịch vụ", "message", "Có " + pendingRequests + " yêu cầu đang chờ xử lý."));
        }

        // 2. Khách sắp check-out hôm nay
        long checkoutsToday = reservationRepository.countByCheckOutDateAndStatus(LocalDate.now(), "CHECKED_IN");
        if (checkoutsToday > 0) {
            notis.add(Map.of("type", "CHECKOUT", "title", "Check-out hôm nay", "message", "Có " + checkoutsToday + " phòng dự kiến trả hôm nay."));
        }
        
        return notis;
    }

    // --- BỔ SUNG: BÁO CÁO & PHÂN TÍCH (ANALYTICS) ---

    // 1. Báo cáo công suất phòng 6 tháng gần nhất
    public Map<String, Object> getOccupancyHistory() {
        Map<String, Object> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();

        long totalRooms = roomRepository.count();
        if (totalRooms == 0) totalRooms = 1; // Tránh chia cho 0

        // Lặp qua 6 tháng gần nhất (từ quá khứ đến hiện tại)
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDate startOfMonth = ym.atDay(1);
            LocalDate endOfMonth = ym.atEndOfMonth();
            int daysInMonth = ym.lengthOfMonth();

            // Lấy tất cả đơn đặt phòng có dính dáng đến tháng này
            List<Reservation> reservations = reservationRepository.findReservationsInPeriod(startOfMonth, endOfMonth);

            long occupiedRoomNights = 0;
            for (Reservation res : reservations) {
                // Tính số ngày khách ở thực tế TRONG tháng này
                LocalDate overlapStart = res.getCheckInDate().isAfter(startOfMonth) ? res.getCheckInDate() : startOfMonth;
                LocalDate overlapEnd = res.getCheckOutDate().isBefore(endOfMonth) ? res.getCheckOutDate() : endOfMonth;
                
                // Cộng thêm 1 ngày vì check-out date thường là ngày khách đi, nhưng đêm trước đó vẫn tính
                // Tuy nhiên logic ChronoUnit.DAYS.between(start, end) tính khoảng cách, nên ta cần cẩn thận.
                // Logic chuẩn: Số đêm = days between. Nếu check-in = check-out (không thể xảy ra do validate), là 0.
                long nights = ChronoUnit.DAYS.between(overlapStart, overlapEnd);
                if (res.getCheckOutDate().isAfter(endOfMonth)) nights++; // Cộng bù nếu khách ở qua tháng
                if (nights < 0) nights = 0;
                
                occupiedRoomNights += nights;
            }

            double occupancyRate = ((double) occupiedRoomNights / (totalRooms * daysInMonth)) * 100;
            
            labels.add("Tháng " + ym.getMonthValue());
            data.add(Math.round(occupancyRate * 100.0) / 100.0); // Làm tròn 2 chữ số
        }

        result.put("labels", labels);
        result.put("data", data);
        return result;
    }

    // 2. Top khách hàng thân thiết (Dựa trên điểm Loyalty)
    public List<Guest> getTopLoyalGuests() {
        // Vì không sửa GuestRepository, ta dùng Java Stream để sort (Chấp nhận được với dataset nhỏ của đồ án)
        return guestRepository.findAll().stream()
                .sorted((g1, g2) -> {
                    Integer p1 = g1.getLoyaltyPoints();
                    Integer p2 = g2.getLoyaltyPoints();
                    return p2.compareTo(p1); // Giảm dần
                })
                .limit(5)
                .collect(Collectors.toList());
    }
}