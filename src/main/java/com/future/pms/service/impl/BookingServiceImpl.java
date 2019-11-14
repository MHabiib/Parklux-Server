package com.future.pms.service.impl;

import com.future.pms.model.Booking;
import com.future.pms.model.Customer;
import com.future.pms.model.Receipt;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.BookingRepository;
import com.future.pms.repository.CustomerRepository;
import com.future.pms.repository.ParkingSlotRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Calendar;

import static com.future.pms.Constants.*;
import static com.future.pms.Utils.getTotalTime;

@Service public class BookingServiceImpl implements BookingService {
    @Autowired ParkingSlotRepository parkingSlotRepository;
    @Autowired BookingRepository bookingRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired ParkingZoneRepository parkingZoneRepository;

    @Override public ResponseEntity loadAll() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @Override public ResponseEntity findBookingCustomer(Principal principal) {
        Customer customer = customerRepository.findByEmail(principal.getName());
        return ResponseEntity.ok(bookingRepository.findBookingByIdUser(customer.getIdCustomer()));
    }

    @Override public ResponseEntity findOngoingBookingCustomer(Principal principal) {
        Customer customer = customerRepository.findByEmail(principal.getName());
        return ResponseEntity
            .ok(bookingRepository.findBookingByIdUserAndDateOut(customer.getIdCustomer(), null));
    }

    @Override public ResponseEntity createBooking(Principal principal, String idSlot) {
        Customer customer = customerRepository.findByEmail(principal.getName());
        ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(idSlot);
        if (SCAN_ME.equals(parkingSlot.getStatus())) {
            parkingSlot.setStatus(BOOKED);
            Booking bookingParking = new Booking();
            bookingParking.setParkingZoneName(
                parkingZoneRepository.findParkingZoneByIdParkingZone(parkingSlot.getIdParkingZone())
                    .getName());
            bookingParking.setPrice(
                parkingZoneRepository.findParkingZoneByIdParkingZone(parkingSlot.getIdParkingZone())
                    .getPrice());
            bookingParking.setIdParkingZone(parkingSlot.getIdParkingZone());
            bookingParking.setSlotName(parkingSlot.getName());
            bookingParking.setIdUser(customer.getIdCustomer());
            bookingParking.setIdSlot(idSlot);
            bookingParking.setIdParkingZone(parkingSlot.getIdParkingZone());
            bookingParking.setDateIn(Calendar.getInstance().getTimeInMillis());
            parkingSlotRepository.save(parkingSlot);
            bookingRepository.save(bookingParking);
            return ResponseEntity.ok().body(bookingParking);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override public ResponseEntity bookingReceipt(String idBooking) {
        Booking booking = bookingRepository.findBookingByIdBooking(idBooking);
        ParkingZone parkingZone =
            parkingZoneRepository.findParkingZoneByIdParkingZone(booking.getIdParkingZone());
        Receipt receipt = new Receipt();
        receipt.setIdBooking(booking.getIdBooking());
        receipt.setParkingZoneName(parkingZone.getName());
        receipt.setAddress(parkingZone.getAddress());
        receipt.setSlotName(booking.getSlotName());
        receipt.setPrice(booking.getPrice());
        receipt.setTotalMinutes((Integer.valueOf(booking.getTotalTime()) % 60) + 1);
        receipt.setTotalHours(
            (Integer.valueOf(booking.getTotalTime()) - receipt.getTotalMinutes()) / 60);
        receipt.setDateIn(booking.getDateIn());
        receipt.setDateOut(booking.getDateOut());
        receipt.setTotalPrice(getTotalPrice(booking.getTotalTime(), booking.getPrice()));
        return ResponseEntity.ok().body(receipt);
    }

    private String getTotalPrice(String totalTime, Double price) {
        int totalMinutes = Integer.valueOf(totalTime) % 60;
        int totalHours = (Integer.valueOf(totalTime) - totalMinutes) / 60;
        if (totalMinutes != 0)
            totalHours += 1;
        return String.valueOf(totalHours * price);
    }

    @Override public ResponseEntity checkoutBooking(String idBooking) {
        Booking bookingExist = bookingRepository.findBookingByIdBooking(idBooking);
        if (null != bookingExist) {
            ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(bookingExist.getIdSlot());
            if (BOOKED.equals(parkingSlot.getStatus())) {
                bookingExist.setDateOut(Calendar.getInstance().getTimeInMillis());
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
