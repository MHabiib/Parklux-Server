package com.future.pms.controller;

import com.future.pms.model.Booking;
import com.future.pms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin("**") @RestController @RequestMapping("/api/booking") public class BookingController {
    @Autowired private BookingService bookingService;

    @GetMapping public ResponseEntity loadAll(Integer page) {
        return ResponseEntity.ok(bookingService.loadAll(page));
    }

    @GetMapping("/customer") public ResponseEntity findBookingCustomer(Principal principal) {
        return bookingService.findBookingCustomerNonPaging(principal);
    }

    /*@GetMapping("/customer")
    public ResponseEntity findBookingCustomerPaging(Principal principal, Integer page) {
        return bookingService.findBookingCustomer(principal, page);
    }*/

    @GetMapping("/customer/ongoing")
    public ResponseEntity findOngoingBookingCustomer(Principal principal) {
        return bookingService.findOngoingBookingCustomer(principal);
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity bookingReceipt(@PathVariable("id") String id) {
        return bookingService.bookingReceipt(id);
    }

    @PostMapping
    public ResponseEntity createBooking(Principal principal, @RequestBody String idSlot) {
        return bookingService.createBooking(principal, idSlot);
    }

    @PostMapping("/checkout") public ResponseEntity checkoutBooking(Principal principal) {
        return bookingService.checkoutBooking(principal);
    }

    @PutMapping("/{id}") public ResponseEntity<Booking> updateBooking(@PathVariable("id") String id,
        @RequestBody Booking booking) {
        return bookingService.updateBooking(id, booking);
    }
}
