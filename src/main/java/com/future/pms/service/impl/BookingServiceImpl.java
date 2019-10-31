package com.future.pms.service.impl;

import com.future.pms.model.Booking;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.repository.BookingRepository;
import com.future.pms.repository.ParkingSlotRepository;
import com.future.pms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;

import static com.future.pms.Constants.BOOKED;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    ParkingSlotRepository parkingSlotRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Override
    public ResponseEntity loadAll() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @Override
    public ResponseEntity createBooking(Booking booking) {
        booking.setDateIn(Calendar.getInstance().getTimeInMillis());
        ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(booking.getIdSlot());
        parkingSlot.setStatus(BOOKED);
        bookingRepository.save(booking);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Booking> updateBooking(String id, Booking booking) {
        return null;
    }

    @Override
    public ResponseEntity deleteBooking(String id) {
        return null;
    }

}
