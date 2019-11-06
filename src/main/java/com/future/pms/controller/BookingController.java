package com.future.pms.controller;

import com.future.pms.model.Booking;
import com.future.pms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("**") @RestController @RequestMapping("/api/booking") public class BookingController {

    @Autowired private BookingService bookingService;

    @GetMapping public ResponseEntity loadAll() {
        return ResponseEntity.ok(bookingService.loadAll());
    }

    @PostMapping public ResponseEntity createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @PostMapping("/checkout") public ResponseEntity checkoutBooking(@RequestBody String idBooking) {
        return bookingService.checkoutBooking(idBooking);
    }

    @PutMapping("/{id}") public ResponseEntity<Booking> updateBooking(@PathVariable("id") String id,
        @RequestBody Booking booking) {
        return bookingService.updateBooking(id, booking);
    }
}
