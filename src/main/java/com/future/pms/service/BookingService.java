package com.future.pms.service;

import com.future.pms.model.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface BookingService {
    ResponseEntity<Booking> loadAll();

    ResponseEntity createBooking(@RequestBody Booking booking);

    ResponseEntity<Booking> updateBooking(@PathVariable("id") String id, @RequestBody Booking booking);

    ResponseEntity deleteBooking(@PathVariable("id") String id);
}
