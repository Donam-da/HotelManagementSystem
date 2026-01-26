package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.exception.ResourceNotFoundException;
import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.ReservationRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BillingService billingService;

    // --- 1. TẠO ĐƠN ĐẶT PHÒNG MỚI ---
    public Reservation createReservation(Reservation reservation, Long guestId, Long roomId) {
        // A. Validate ngày tháng
        if (reservation.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Ngày Check-in không được ở quá khứ!");
        }
        if (reservation.getCheckOutDate().isBefore(reservation.getCheckInDate().plusDays(1))) {
            throw new RuntimeException("Ngày Check-out phải sau ngày Check-in ít nhất 1 đêm!");
        }

        // B. Tìm Khách hàng & Phòng (Trả về 404 nếu không thấy)
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại với ID: " + guestId));
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng không tồn tại với ID: " + roomId));

        // C. Kiểm tra phòng có trống không?
        boolean isAvailable = checkRoomAvailability(roomId, reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (!isAvailable) {
            throw new RuntimeException("Phòng " + room.getRoomNumber() + " đã bị đặt trong khoảng thời gian này!");
        }

        // D. Gán thông tin
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setStatus("CONFIRMED");

        // E. Lưu đơn đặt
        Reservation savedReservation = reservationRepository.save(reservation);

        // F. Tạo hóa đơn tạm tính ngay lập tức
        billingService.generateInvoice(savedReservation);

        return savedReservation;
    }

    // --- 2. HỦY ĐẶT PHÒNG (PATCH cancel) ---
    public Reservation cancelReservation(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));

        if ("CANCELLED".equals(res.getStatus())) {
            throw new RuntimeException("Đơn này đã hủy rồi!");
        }

        // Logic phạt hủy muộn (Ví dụ đơn giản)
        long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), res.getCheckInDate());
        if (daysUntilCheckIn < 1) {
            System.out.println("LOG: Khách hủy sát ngày, có thể tính phí phạt tại đây.");
        }

        res.setStatus("CANCELLED");
        return reservationRepository.save(res);
    }

    // --- 3. CHECK-IN (PATCH check-in) ---
    public Reservation checkIn(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));
        
        // Kiểm tra logic trạng thái
        if ("CANCELLED".equals(res.getStatus())) {
             throw new RuntimeException("Không thể Check-in đơn đã hủy!");
        }

        res.setStatus("CHECKED_IN");
        return reservationRepository.save(res);
    }

    // --- 4. CHECK-OUT (PATCH check-out) ---
    public Reservation checkOut(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));

        if (!"CHECKED_IN".equals(res.getStatus())) {
            throw new RuntimeException("Lỗi: Khách chưa Check-in nên không thể Check-out!");
        }

        // 1. Cập nhật trạng thái
        res.setStatus("CHECKED_OUT");
        Reservation savedRes = reservationRepository.save(res);

        // 2. QUAN TRỌNG: Tính toán lại hóa đơn lần cuối
        // (Để cập nhật tổng tiền bao gồm các dịch vụ gọi thêm trong lúc ở)
        billingService.generateInvoice(savedRes);

        return savedRes;
    }

    // --- 5. LẤY HÓA ĐƠN (GET invoice) ---
    public Invoice getInvoiceByReservationId(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));
        
        // Luôn đảm bảo có hóa đơn
        if (res.getInvoice() == null) {
            return billingService.generateInvoice(res);
        }
        return res.getInvoice();
    }

    // --- 6. HÀM PHỤ TRỢ: KIỂM TRA PHÒNG TRỐNG ---
    private boolean checkRoomAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        List<Room> availableRooms = roomRepository.findAvailableRooms(checkIn, checkOut);
        // Kiểm tra xem phòng mình chọn có nằm trong danh sách phòng trống không
        return availableRooms.stream().anyMatch(r -> r.getId().equals(roomId));
    }
    
    // --- 7. LẤY TẤT CẢ ---
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}