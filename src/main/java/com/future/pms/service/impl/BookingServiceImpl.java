package com.future.pms.service.impl;

import com.future.pms.Utils;
import com.future.pms.model.Booking;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.repository.BookingRepository;
import com.future.pms.repository.ParkingSlotRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;

import static com.future.pms.Constants.*;
import static com.future.pms.Utils.getTotalTime;

@Service public class BookingServiceImpl implements BookingService {

    @Autowired ParkingSlotRepository parkingSlotRepository;

    @Autowired BookingRepository bookingRepository;

    @Autowired ParkingZoneRepository parkingZoneRepository;

    @Override public ResponseEntity loadAll() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @Override public ResponseEntity createBooking(Booking booking) {
        ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(booking.getIdSlot());
        if (SCAN_ME.equals(parkingSlot.getStatus())) {
            parkingSlot.setStatus(BOOKED);
            Booking bookingParking = new Booking();
            bookingParking.setIdUser(booking.getIdUser());
            bookingParking.setIdSlot(booking.getIdSlot());
            bookingParking.setIdParkingZone(parkingSlot.getIdParkingZone());
            bookingParking.setDateIn(Calendar.getInstance().getTimeInMillis());
            parkingSlotRepository.save(parkingSlot);
            bookingRepository.save(bookingParking);
            return ResponseEntity.ok().body(bookingParking);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override public ResponseEntity checkoutBooking(String idBooking) {
        Booking bookingExist = bookingRepository.findBookingByIdBooking(idBooking);
        if (null != bookingExist) {
            ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(bookingExist.getIdSlot());
            if (BOOKED.equals(parkingSlot.getStatus())) {
                bookingExist.setParkingZoneName(parkingZoneRepository
                    .findParkingZoneByIdParkingZone(parkingSlot.getIdParkingZone()).getName());
                bookingExist.setPrice(parkingZoneRepository
                    .findParkingZoneByIdParkingZone(parkingSlot.getIdParkingZone()).getPrice());
                bookingExist.setDateOut(Calendar.getInstance().getTimeInMillis());
                bookingExist.setIdParkingZone(parkingSlot.getIdParkingZone());
                bookingExist.setSlotName(parkingSlot.getName());
                bookingExist.setTotalTime(Long.toString(
                    getTotalTime(bookingExist.getDateIn(), bookingExist.getDateOut())));
                parkingSlot.setStatus(AVAILABLE);
                parkingSlotRepository.save(parkingSlot);
                bookingRepository.save(bookingExist);
                return ResponseEntity.ok().body(bookingExist);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override public ResponseEntity<Booking> updateBooking(String id, Booking booking) {
        return null;
    }

    @Override public ResponseEntity deleteBooking(String id) {
        return null;
    }

}
