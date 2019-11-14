package com.future.pms.service;

import com.future.pms.model.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

public interface BookingService {
    ResponseEntity<Booking> loadAll();

    ResponseEntity<Booking> findBookingCustomer(Principal principal);

    ResponseEntity findOngoingBookingCustomer(Principal principal);

    ResponseEntity createBooking(Principal principal, @RequestBody String idSlot);

    ResponseEntity bookingReceipt(@PathVariable("id") String id);

    ResponseEntity checkoutBooking(@RequestBody String idBooking);

    ResponseEntity<Booking> updateBooking(@PathVariable("id") String id,
        @RequestBody Booking booking);

    ResponseEntity deleteBooking(@PathVariable("id") String id);
}
