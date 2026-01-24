package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.Reservation;
import com.example.hotelmanagement.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    // Tạo đơn đặt phòng
    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation, @RequestParam Long guestId) {
        return reservationService.createReservation(reservation, guestId);
    }
}