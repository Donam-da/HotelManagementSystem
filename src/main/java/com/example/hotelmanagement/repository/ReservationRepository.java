package com.example.hotelmanagement.repository;
import com.example.hotelmanagement.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // Giải quyết vấn đề N+1 Query bằng JOIN FETCH
    @Query(value = "SELECT r FROM Reservation r JOIN FETCH r.guest JOIN FETCH r.room JOIN FETCH r.room.roomType", countQuery = "SELECT count(r) FROM Reservation r")
    List<Reservation> findAllWithDetails();

    // Hỗ trợ phân trang và tránh N+1 (Yêu cầu 5.2 & 5.3)
    @EntityGraph(attributePaths = {"guest", "room", "room.roomType"})
    @NonNull
    Page<Reservation> findAll(@NonNull Pageable pageable);

    long countByCheckOutDateAndStatus(LocalDate checkOutDate, String status);

    // Tìm đơn đặt phòng đang hoạt động (CHECKED_IN) của một phòng để lấy thông tin nhanh
    Reservation findFirstByRoomIdAndStatus(Long roomId, String status);

    // Tìm các đơn đặt phòng có thời gian lưu trú trùng với khoảng thời gian [startDate, endDate]
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate < :endDate AND r.checkOutDate > :startDate AND r.status <> 'CANCELLED'")
    List<Reservation> findReservationsInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}