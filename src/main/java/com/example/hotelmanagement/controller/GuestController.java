package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/guests")
public class GuestController {

    @Autowired
    private GuestService guestService;

    // API tạo khách hàng mới
    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        return guestService.createGuest(guest);
    }

    // API lấy danh sách khách hàng
    @GetMapping
    public List<Guest> getAllGuests() {
        return guestService.getAllGuests();
    }
}