package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    boolean existsByEmail(String email);
    Page<Guest> findByIsDeletedFalse(Pageable pageable);
    
    @Query("SELECT g FROM Guest g WHERE g.isDeleted = false AND " +
           "(:keyword IS NULL OR LOWER(g.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(g.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(g.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Guest> searchGuests(String keyword);
}