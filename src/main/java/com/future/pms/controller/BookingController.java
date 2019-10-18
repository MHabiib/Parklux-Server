package com.future.pms.controller;

import com.future.pms.model.Booking;
import com.future.pms.model.User;
import com.future.pms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("**")
@RestController
@RequestMapping("/api/booking")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @GetMapping
    public ResponseEntity loadAll() {
        return ResponseEntity.ok(bookingService.loadAll());
    }

    @PostMapping
    public ResponseEntity createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable("id") String id, @RequestBody Booking booking) {
        return bookingService.updateBooking(id,booking);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteBooking(@PathVariable("id") String id) {
        return bookingService.deleteBooking(id);
    }
}
