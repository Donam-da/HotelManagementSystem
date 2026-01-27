package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.exception.ResourceNotFoundException;
import com.example.hotelmanagement.exception.BusinessException;
import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.ReservationRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
            throw new BusinessException("Ngày Check-in không được ở quá khứ!");
        }
        if (reservation.getCheckOutDate().isBefore(reservation.getCheckInDate().plusDays(1))) {
            throw new BusinessException("Ngày Check-out phải sau ngày Check-in ít nhất 1 đêm!");
        }

        // B. Tìm Khách hàng & Phòng (Trả về 404 nếu không thấy)
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại với ID: " + guestId));
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng không tồn tại với ID: " + roomId));

        // C. Kiểm tra phòng có trống không?
        boolean isAvailable = checkRoomAvailability(roomId, reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (!isAvailable) {
            throw new BusinessException("Phòng " + room.getRoomNumber() + " đã bị đặt trong khoảng thời gian này!");
        }

        // D. Gán thông tin
        reservation.setGuest(guest);
        reservation.setRoom(room);
        // THAY ĐỔI: Mặc định là PENDING (Chờ xử lý) theo yêu cầu 4.2.3
        reservation.setStatus("PENDING");

        // E. Lưu đơn đặt
        Reservation savedReservation = reservationRepository.save(reservation);

        // F. Tạo hóa đơn tạm tính ngay lập tức
        billingService.generateInvoice(savedReservation);

        return savedReservation;
    }

    // --- BỔ SUNG: XÁC NHẬN ĐẶT PHÒNG (Pending -> Confirmed) ---
    public Reservation confirmReservation(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn không tồn tại"));
        res.setStatus("CONFIRMED");
        return reservationRepository.save(res);
    }

    // --- 2. HỦY ĐẶT PHÒNG (PATCH cancel) ---
    public Reservation cancelReservation(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));

        if ("CANCELLED".equals(res.getStatus())) {
            throw new BusinessException("Đơn này đã hủy rồi!");
        }

        // Logic phạt hủy muộn (Ví dụ đơn giản)
        long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), res.getCheckInDate());
        if (daysUntilCheckIn < 1) {
            System.out.println("LOG: Khách hủy sát ngày, có thể tính phí phạt tại đây.");
        }

        res.setStatus("CANCELLED");
        Reservation savedRes = reservationRepository.save(res);
        
        // Cập nhật hóa đơn để tính phí phạt hủy (nếu có)
        billingService.generateInvoice(savedRes);
        
        return savedRes;
    }

    // --- 3. CHECK-IN (PATCH check-in) ---
    public Reservation checkIn(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));
        
        // Kiểm tra logic trạng thái
        if (!"CONFIRMED".equals(res.getStatus())) {
             throw new BusinessException("Chỉ đơn hàng đã xác nhận (CONFIRMED) mới được Check-in!");
        }

        res.setStatus("CHECKED_IN");
        
        // [FIX] Cập nhật trạng thái phòng thành OCCUPIED để báo cáo chạy đúng
        Room room = res.getRoom();
        room.setStatus("OCCUPIED");
        roomRepository.save(room);
        
        return reservationRepository.save(res);
    }

    // --- 4. CHECK-OUT (PATCH check-out) ---
    public Reservation checkOut(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));

        if (!"CHECKED_IN".equals(res.getStatus())) {
            throw new BusinessException("Lỗi: Khách chưa Check-in nên không thể Check-out!");
        }

        // 1. Cập nhật trạng thái
        res.setStatus("CHECKED_OUT");
        
        // [FIX] Trả phòng về trạng thái AVAILABLE
        Room room = res.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);
        
        Reservation savedRes = reservationRepository.save(res);

        // 2. QUAN TRỌNG: Tính toán lại hóa đơn lần cuối
        // (Để cập nhật tổng tiền bao gồm các dịch vụ gọi thêm trong lúc ở)
        Invoice finalInvoice = billingService.generateInvoice(savedRes);

        // 3. TÍCH ĐIỂM THÀNH VIÊN (LOYALTY PROGRAM)
        // Logic: 100,000 VNĐ = 1 điểm
        if (finalInvoice.getTotalAmount() != null) {
            Guest guest = savedRes.getGuest();
            int pointsEarned = (int) (finalInvoice.getTotalAmount() / 100000);
            guest.setLoyaltyPoints(guest.getLoyaltyPoints() + pointsEarned);
            guestRepository.save(guest);
        }

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
        return reservationRepository.findAllWithDetails(); // Sử dụng query tối ưu N+1
    }

    // --- 7b. LẤY TẤT CẢ CÓ PHÂN TRANG (Yêu cầu 5.2) ---
    public Page<Reservation> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }

    // --- 8. SỬA ĐỔI ĐẶT PHÒNG (Modification) ---
    // UC-004: Modify Reservation (Dates & Room)
    public Reservation modifyReservation(Long reservationId, LocalDate newCheckIn, LocalDate newCheckOut, Long newRoomId, Long newGuestId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn không tồn tại"));

        if (!"CONFIRMED".equals(res.getStatus())) {
            throw new BusinessException("Chỉ có thể sửa đơn ở trạng thái CONFIRMED");
        }

        // 1. Cập nhật ngày tháng (nếu có thay đổi)
        if (newCheckIn != null && newCheckOut != null) {
            // Validate logic ngày
            if (newCheckIn.isBefore(LocalDate.now())) throw new BusinessException("Ngày Check-in không hợp lệ");
            if (newCheckOut.isBefore(newCheckIn.plusDays(1))) throw new BusinessException("Ngày Check-out không hợp lệ");

            // Nếu KHÔNG đổi phòng (tức là giữ phòng cũ), phải kiểm tra xem ngày mới có bị trùng không
            if (newRoomId == null || newRoomId.equals(res.getRoom().getId())) {
                long overlap = roomRepository.countOverlappingReservations(res.getRoom().getId(), newCheckIn, newCheckOut, reservationId);
                if (overlap > 0) {
                    throw new BusinessException("Phòng hiện tại đã kín lịch trong khoảng thời gian mới chọn!");
                }
            }
            res.setCheckInDate(newCheckIn);
            res.setCheckOutDate(newCheckOut);
        }

        // 2. Cập nhật phòng (nếu khách muốn đổi phòng khác)
        if (newRoomId != null && !newRoomId.equals(res.getRoom().getId())) {
            Room newRoom = roomRepository.findById(newRoomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Phòng mới không tồn tại"));
            
            // Kiểm tra xem phòng MỚI có trống không (dùng hàm countOverlappingReservations cho chính xác)
            long overlap = roomRepository.countOverlappingReservations(newRoomId, res.getCheckInDate(), res.getCheckOutDate(), reservationId);
            if (overlap > 0) {
                throw new BusinessException("Phòng " + newRoom.getRoomNumber() + " đã có người đặt trong thời gian này!");
            }
            res.setRoom(newRoom);
        }

        // 3. Cập nhật khách hàng (nếu có thay đổi)
        if (newGuestId != null && !newGuestId.equals(res.getGuest().getId())) {
            Guest newGuest = guestRepository.findById(newGuestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Khách hàng mới không tồn tại"));
            res.setGuest(newGuest);
        }
        
        // Cập nhật lại hóa đơn tạm tính
        billingService.generateInvoice(res);
        
        return reservationRepository.save(res);
    }

    // Phương thức cũ để tương thích với Controller (chỉ đổi ngày)
    public Reservation changeReservationDates(Long reservationId, LocalDate newCheckIn, LocalDate newCheckOut) {
        return modifyReservation(reservationId, newCheckIn, newCheckOut, null, null);
    }

    // --- 9. XỬ LÝ NO-SHOW (Khách không đến) ---
    // UC-005 / 4.2.3: Manage reservation statuses (No-Show)
    public Reservation markAsNoShow(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn không tồn tại"));

        if (!"CONFIRMED".equals(res.getStatus())) {
            throw new BusinessException("Chỉ đơn CONFIRMED mới có thể đánh dấu No-Show");
        }

        res.setStatus("NO_SHOW");
        // Có thể thêm logic tính phí phạt 100% đêm đầu tiên tại đây nếu cần
        
        Reservation savedRes = reservationRepository.save(res);
        
        // Cập nhật hóa đơn để tính phí phạt No-Show
        billingService.generateInvoice(savedRes);
        
        return savedRes;
    }

    // --- 10. LẤY CHI TIẾT (Hỗ trợ Controller tuân thủ Layered Architecture) ---
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + id));
    }
}