package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.GuestDTO;
import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @PostMapping
    public ResponseEntity<GuestDTO> registerGuest(@RequestBody Guest guest) {
        Guest savedGuest = guestService.registerGuest(guest);
        return ResponseEntity.ok(guestService.convertToDTO(savedGuest));
    }

    @GetMapping
    public ResponseEntity<Page<GuestDTO>> getAllGuests(Pageable pageable) {
        Page<Guest> guests = guestService.getAllGuests(pageable);
        return ResponseEntity.ok(guests.map(guestService::convertToDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestDTO> getGuestById(@PathVariable Long id) {
        Guest guest = guestService.getGuestById(id);
        return ResponseEntity.ok(guestService.convertToDTO(guest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuestDTO> updateGuest(@PathVariable Long id, @RequestBody Guest guestDetails) {
        Guest updatedGuest = guestService.updateGuestProfile(id, guestDetails);
        return ResponseEntity.ok(guestService.convertToDTO(updatedGuest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<GuestDTO>> searchGuests(@RequestParam String keyword) {
        List<Guest> guests = guestService.searchGuests(keyword);
        return ResponseEntity.ok(guests.stream().map(guestService::convertToDTO).collect(Collectors.toList()));
    }
}