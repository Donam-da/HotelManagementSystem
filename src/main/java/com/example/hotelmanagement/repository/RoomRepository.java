package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // --- ĐÂY LÀ ĐOẠN CODE CÒN THIẾU GÂY RA LỖI ---
    
    // Tìm các phòng KHÔNG nằm trong danh sách đặt phòng (trùng ngày)
    @Query("SELECT r FROM Room r WHERE r.id NOT IN (" +
           "  SELECT res.room.id FROM Reservation res " +
           "  WHERE res.status != 'CANCELLED' " +
           "  AND ((res.checkInDate < :checkOut) AND (res.checkOutDate > :checkIn))" +
           ")")
    List<Room> findAvailableRooms(@Param("checkIn") LocalDate checkIn, 
                                  @Param("checkOut") LocalDate checkOut);

    // --- BỔ SUNG CHO UC-002: TÌM KIẾM NÂNG CAO ---
    @Query("SELECT r FROM Room r WHERE " +
           "(:roomTypeId IS NULL OR r.roomType.id = :roomTypeId) AND " +
           "(:capacity IS NULL OR r.roomType.maxOccupancy >= :capacity) AND " +
           "r.id NOT IN (" +
           "  SELECT res.room.id FROM Reservation res " +
           "  WHERE res.status != 'CANCELLED' " +
           "  AND ((res.checkInDate < :checkOut) AND (res.checkOutDate > :checkIn))" +
           ")")
    List<Room> searchAvailableRooms(@Param("checkIn") LocalDate checkIn, 
                                    @Param("checkOut") LocalDate checkOut,
                                    @Param("roomTypeId") Long roomTypeId,
                                    @Param("capacity") Integer capacity);

    // --- BỔ SUNG CHO UC-004: KIỂM TRA TRÙNG LỊCH KHI SỬA ĐƠN ---
    @Query("SELECT COUNT(res) FROM Reservation res WHERE " +
           "res.room.id = :roomId " +
           "AND res.status != 'CANCELLED' " +
           "AND res.id != :excludeReservationId " + // Loại trừ chính đơn đang sửa
           "AND ((res.checkInDate < :checkOut) AND (res.checkOutDate > :checkIn))")
    long countOverlappingReservations(@Param("roomId") Long roomId, 
                                      @Param("checkIn") LocalDate checkIn, 
                                      @Param("checkOut") LocalDate checkOut,
                                      @Param("excludeReservationId") Long excludeReservationId);

    // --- BỔ SUNG CHO REPORT SERVICE (Performance Optimization) ---
    long countByStatus(String status);
}