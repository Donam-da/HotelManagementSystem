package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.InventoryItem;
import com.example.hotelmanagement.repository.InventoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Quản lý kho vật tư tiêu hao")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    public List<InventoryItem> getAllItems() {
        return inventoryRepository.findAll();
    }

    @PostMapping
    public InventoryItem addItem(@RequestBody InventoryItem item) {
        return inventoryRepository.save(Objects.requireNonNull(item));
    }

    @PutMapping("/{id}/stock")
    public InventoryItem updateStock(@PathVariable Long id, @RequestParam Integer quantityChange) {
        InventoryItem item = inventoryRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setQuantity(item.getQuantity() + quantityChange);
        return inventoryRepository.save(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryRepository.deleteById(Objects.requireNonNull(id));
        return ResponseEntity.noContent().build();
    }
}