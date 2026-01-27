package com.example.hotelmanagement.repository;
import com.example.hotelmanagement.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    // Tìm khách theo email (để check trùng)
    boolean existsByEmail(String email);

    // --- BỔ SUNG THEO YÊU CẦU 4.2.1 (Search by criteria) ---
    @Query("SELECT g FROM Guest g WHERE g.firstName LIKE %:keyword% OR g.lastName LIKE %:keyword% OR g.email LIKE %:keyword% OR g.phone LIKE %:keyword%")
    List<Guest> searchGuests(@Param("keyword") String keyword);
}