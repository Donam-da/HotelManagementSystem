package com.example.hotelmanagement.service;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.entity.Invoice;
import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.dto.ReservationDTO;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomStatus;
import com.example.hotelmanagement.exception.ResourceNotFoundException;
import com.example.hotelmanagement.exception.BusinessException;
import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.ReservationRepository;
import com.example.hotelmanagement.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final BillingService billingService;

    public ReservationService(ReservationRepository reservationRepository,
                              GuestRepository guestRepository,
                              RoomRepository roomRepository,
                              BillingService billingService) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.billingService = billingService;
    }

    // --- 1. TẠO ĐƠN ĐẶT PHÒNG MỚI ---
    @Transactional
    public Reservation createReservation(Reservation reservation, Long guestId, Long roomId) {
        // A. Validate ngày tháng
        if (reservation.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Ngày Check-in không được ở quá khứ!");
        }
        if (reservation.getCheckOutDate().isBefore(reservation.getCheckInDate().plusDays(1))) {
            throw new BusinessException("Ngày Check-out phải sau ngày Check-in ít nhất 1 đêm!");
        }
        
        // BR-002: Maximum duration is 30 nights
        if (ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate()) > 30) {
            throw new BusinessException("Thời gian đặt phòng tối đa là 30 đêm liên tiếp!");
        }

        // B. Tìm Khách hàng & Phòng (Trả về 404 nếu không thấy)
        Guest guest = guestRepository.findById(Objects.requireNonNull(guestId))
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại với ID: " + guestId));
        
        // BR-101: Guest must be at least 18 years old
        if (guest.getDateOfBirth() != null) {
            long age = ChronoUnit.YEARS.between(guest.getDateOfBirth(), LocalDate.now());
            if (age < 18) {
                throw new BusinessException("Khách hàng phải từ 18 tuổi trở lên để đặt phòng!");
            }
        }

        Room room = roomRepository.findById(Objects.requireNonNull(roomId))
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
    @Transactional
    public Reservation confirmReservation(Long reservationId) {
        Reservation res = reservationRepository.findById(Objects.requireNonNull(reservationId))
                .orElseThrow(() -> new ResourceNotFoundException("Đơn không tồn tại"));
        res.setStatus("CONFIRMED");
        return reservationRepository.save(res);
    }

    // --- 2. HỦY ĐẶT PHÒNG (PATCH cancel) ---
    @Transactional
    public Reservation cancelReservation(Long reservationId) {
        Reservation res = reservationRepository.findById(Objects.requireNonNull(reservationId))
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));

        if ("CANCELLED".equals(res.getStatus())) {
            throw new BusinessException("Đơn này đã hủy rồi!");
        }

        // BR-005, BR-006, BR-007: Tính phí phạt hủy
        // Giả sử giờ check-in tiêu chuẩn là 14:00
        LocalDateTime checkInTime = res.getCheckInDate().atTime(14, 0);
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilCheckIn = ChronoUnit.HOURS.between(now, checkInTime);
        
        Double oneNightPrice = res.getRoom().getRoomType().getBasePrice();
        Double fee = 0.0;

        if (hoursUntilCheckIn < 24) {
            // BR-005: < 24h -> Phạt 1 đêm
            fee = oneNightPrice;
        } else if (hoursUntilCheckIn < 72) {
            // BR-006: 24h-72h -> Phạt 50% đêm đầu
            fee = oneNightPrice * 0.5;
        }
        // BR-007: > 72h -> Miễn phí (fee = 0.0)

        res.setCancellationFee(fee);
        res.setStatus("CANCELLED");
        Reservation savedRes = reservationRepository.save(res);
        
        // Cập nhật hóa đơn để tính phí phạt hủy (nếu có)
        billingService.generateInvoice(savedRes);
        
        return savedRes;
    }

    // --- 3. CHECK-IN (PATCH check-in) ---
    @Transactional
    public Reservation checkIn(Long reservationId) {
        Reservation res = reservationRepository.findById(Objects.requireNonNull(reservationId))
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));
        
        // Kiểm tra logic trạng thái
        if (!"CONFIRMED".equals(res.getStatus())) {
             throw new BusinessException("Chỉ đơn hàng đã xác nhận (CONFIRMED) mới được Check-in!");
        }

        res.setStatus("CHECKED_IN");
        
        // [FIX] Cập nhật trạng thái phòng thành OCCUPIED để báo cáo chạy đúng
        Room room = res.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);
        
        return reservationRepository.save(res);
    }

    // --- 4. CHECK-OUT (PATCH check-out) ---
    @Transactional
    public Reservation checkOut(Long reservationId) {
        Reservation res = reservationRepository.findById(Objects.requireNonNull(reservationId))
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));

        if (!"CHECKED_IN".equals(res.getStatus())) {
            throw new BusinessException("Lỗi: Khách chưa Check-in nên không thể Check-out!");
        }

        // 1. Cập nhật trạng thái
        res.setStatus("CHECKED_OUT");
        
        // [FIX] Trả phòng về trạng thái AVAILABLE
        Room room = res.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        
        Reservation savedRes = reservationRepository.save(res);

        // 2. QUAN TRỌNG: Tính toán lại hóa đơn lần cuối
        // (Để cập nhật tổng tiền bao gồm các dịch vụ gọi thêm trong lúc ở)
        Invoice finalInvoice = billingService.generateInvoice(savedRes);

        // 3. TÍCH ĐIỂM THÀNH VIÊN (LOYALTY PROGRAM - BR-103)
        // Rule: 10 points per $1 spent (Giả sử đơn vị tiền tệ là tương đương $)
        if (finalInvoice.getTotalAmount() != null) {
            Guest guest = savedRes.getGuest();
            int pointsEarned = (int) (finalInvoice.getTotalAmount() * 10);
            guest.setLoyaltyPoints(guest.getLoyaltyPoints() + pointsEarned);
            guestRepository.save(guest);
        }

        return savedRes;
    }

    // --- 5. LẤY HÓA ĐƠN (GET invoice) ---
    public Invoice getInvoiceByReservationId(Long reservationId) {
        Reservation res = reservationRepository.findById(Objects.requireNonNull(reservationId))
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + reservationId));
        
        // Luôn đảm bảo có hóa đơn
        if (res.getInvoice() == null) {
            return billingService.generateInvoice(res);
        }
        return res.getInvoice();
    }

    // --- 6. HÀM PHỤ TRỢ: KIỂM TRA PHÒNG TRỐNG ---
    private boolean checkRoomAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        // Sử dụng countOverlappingReservations với excludeReservationId = -1 (không loại trừ ai)
        long overlap = roomRepository.countOverlappingReservations(roomId, checkIn, checkOut, -1L);
        return overlap == 0;
    }
    
    // --- 7. LẤY TẤT CẢ ---
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllWithDetails(); // Sử dụng query tối ưu N+1
    }

    // --- 7b. LẤY TẤT CẢ CÓ PHÂN TRANG (Yêu cầu 5.2) ---
    public Page<Reservation> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(Objects.requireNonNull(pageable));
    }

    // --- 8. SỬA ĐỔI ĐẶT PHÒNG (Modification) ---
    // UC-004: Modify Reservation (Dates & Room)
    @Transactional
    public Reservation modifyReservation(Long reservationId, LocalDate newCheckIn, LocalDate newCheckOut, Long newRoomId, Long newGuestId) {
        Reservation res = reservationRepository.findById(Objects.requireNonNull(reservationId))
                .orElseThrow(() -> new ResourceNotFoundException("Đơn không tồn tại"));

        // BR-004: Allow modification if PENDING or CONFIRMED
        if (!"PENDING".equals(res.getStatus()) && !"CONFIRMED".equals(res.getStatus())) {
            throw new BusinessException("Chỉ có thể sửa đơn ở trạng thái PENDING hoặc CONFIRMED");
        }

        // 1. Cập nhật ngày tháng (nếu có thay đổi)
        if (newCheckIn != null && newCheckOut != null) {
            // Validate logic ngày
            if (newCheckIn.isBefore(LocalDate.now())) throw new BusinessException("Ngày Check-in không hợp lệ");
            if (newCheckOut.isBefore(newCheckIn.plusDays(1))) throw new BusinessException("Ngày Check-out không hợp lệ");
            
            // BR-002: Check max duration
            if (ChronoUnit.DAYS.between(newCheckIn, newCheckOut) > 30) {
                throw new BusinessException("Thời gian đặt phòng tối đa là 30 đêm liên tiếp!");
            }

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
    @Transactional
    public Reservation markAsNoShow(Long reservationId) {
        Reservation res = reservationRepository.findById(Objects.requireNonNull(reservationId))
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
        return reservationRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt phòng không tồn tại với ID: " + id));
    }

    // --- 11. MAPPING ENTITY TO DTO (Thống nhất Clean Code) ---
    public ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setConfirmationNumber(reservation.getConfirmationNumber());
        dto.setCheckInDate(reservation.getCheckInDate());
        dto.setCheckOutDate(reservation.getCheckOutDate());
        dto.setStatus(reservation.getStatus());
        
        if (reservation.getGuest() != null) {
            dto.setGuestName(reservation.getGuest().getFirstName() + " " + reservation.getGuest().getLastName());
        }
        
        if (reservation.getRoom() != null) {
            dto.setRoomNumber(reservation.getRoom().getRoomNumber());
        }
        
        if (reservation.getInvoice() != null) {
            dto.setTotalAmount(reservation.getInvoice().getTotalAmount());
        }
        return dto;
    }
}