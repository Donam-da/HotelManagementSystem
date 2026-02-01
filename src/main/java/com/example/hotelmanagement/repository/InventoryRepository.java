package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
}