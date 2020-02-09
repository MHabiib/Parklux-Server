package com.future.pms.controller;

import com.future.pms.service.BookingService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@CrossOrigin("**") @RestController @RequestMapping public class BookingController {
    @Autowired private BookingService bookingService;

    @GetMapping("/api3/booking") public ResponseEntity loadAll(String filter, Integer page) {
        return bookingService.loadAll(filter, page);
    }

    @GetMapping("/api/booking/customer")
    public ResponseEntity findBookingCustomerPaging(Principal principal, Integer page) {
        return bookingService.findBookingCustomer(principal, page);
    }

    @GetMapping("/api2/booking/ongoing/parking-zone")
    public ResponseEntity findOngoingBookingParkingZone(Principal principal, Integer page) {
        return bookingService.findOngoingBookingParkingZone(principal, page);
    }

    @GetMapping("/api2/booking/past/parking-zone")
    public ResponseEntity findPastBookingParkingZone(Principal principal, Integer page) {
        return bookingService.findPastBookingParkingZone(principal, page);
    }

    @GetMapping("/api/booking/customer/ongoing")
    public ResponseEntity findOngoingBookingCustomer(Principal principal) {
        return bookingService.findOngoingBookingCustomer(principal);
    }

    @GetMapping("/api/booking/{id}/receipt")
    public ResponseEntity bookingReceipt(@PathVariable("id") String id) {
        return bookingService.bookingReceipt(id);
    }

    @GetMapping("/api3/booking/{id}")
    public ResponseEntity findBookingById(@PathVariable("id") String id) {
        return bookingService.findBookingById(id);
    }

    @GetMapping("/api3/booking/{id}/receipt")
    public ResponseEntity bookingReceiptSA(@PathVariable("id") String id) {
        return bookingService.bookingReceipt(id);
    }

    @PostMapping("/api/booking/{id}/{fcm}")
    public ResponseEntity createBooking(Principal principal, @PathVariable("id") String idSlot,
        @PathVariable("fcm") String fcm) throws JSONException {
        return bookingService.createBooking(principal, idSlot, fcm);
    }

    @PostMapping("/api/booking/checkoutStepOne/{fcmToken}")
    public ResponseEntity checkoutBookingStepOne(Principal principal,
        @PathVariable("fcmToken") String fcmToken) throws IOException {
        return bookingService.checkoutBookingStepOne(principal, fcmToken);
    }

    @PostMapping("/api2/booking/checkoutStepTwo/{id}/{fcmToken}")
    public ResponseEntity checkoutBookingStepTwo(Principal principal,
        @PathVariable("id") String idCustomer, @PathVariable("fcmToken") String fcmToken)
        throws JSONException {
        return bookingService.checkoutBookingStepTwo(principal, fcmToken, idCustomer);
    }

    @PostMapping("/api3/booking/{id}/checkout")
    public ResponseEntity checkoutBookingSA(@PathVariable("id") String id) {
        return bookingService.checkoutBookingSA(id);
    }
}
