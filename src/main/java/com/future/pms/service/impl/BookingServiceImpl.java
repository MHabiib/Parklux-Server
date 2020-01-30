package com.future.pms.service.impl;

import com.future.pms.FcmClient;
import com.future.pms.model.Booking;
import com.future.pms.model.Customer;
import com.future.pms.model.Receipt;
import com.future.pms.model.User;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.*;
import com.future.pms.service.BookingService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    @Autowired UserRepository userRepository;
    @Autowired ParkingZoneRepository parkingZoneRepository;
    @Autowired ParkingLevelRepository parkingLevelRepository;


    @Override public ResponseEntity loadAll(String filter, Integer page) {
        switch (filter) {
            case ALL: {
                PageRequest request =
                    PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "dateIn"));
                return ResponseEntity.ok(bookingRepository.findBookingBy(request));
            }
            case ONGOING: {
                PageRequest request =
                    PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "dateIn"));
                return ResponseEntity.ok(bookingRepository.findBookingByDateOutNull(request));
            }
            default: {
                PageRequest request =
                    PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "dateIn"));
                return ResponseEntity.ok(bookingRepository.findBookingByDateOutNotNull(request));
            }
        }
    }

    @Override public ResponseEntity findBookingCustomer(Principal principal, Integer page) {
        Customer customer = customerRepository.findByEmail(principal.getName());
        if (customer != null) {
            PageRequest request = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "dateIn"));
            return ResponseEntity.ok(bookingRepository
                .findBookingByIdUserAndDateOutNotNull(customer.getIdCustomer(), request));
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override public ResponseEntity findOngoingBookingCustomer(Principal principal) {
        Customer customer = customerRepository.findByEmail(principal.getName());
        if (customer != null) {
            return ResponseEntity.ok(bookingRepository
                .findBookingByIdUserAndDateOut(customer.getIdCustomer(), null));
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity findOngoingBookingParkingZone(Principal principal, Integer page) {
        ParkingZone parkingZone =
            parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName());
        PageRequest request = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "dateIn"));
        return ResponseEntity.ok(bookingRepository
            .findBookingByIdParkingZoneAndDateOut(parkingZone.getIdParkingZone(), null, request));
    }

    @Override public ResponseEntity findPastBookingParkingZone(Principal principal, Integer page) {
        ParkingZone parkingZone =
            parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName());
        PageRequest request = PageRequest.of(page, 10, new Sort(Sort.Direction.DESC, "dateOut"));
        return ResponseEntity.ok(bookingRepository
            .findBookingByIdParkingZoneAndDateOutNotNull(parkingZone.getIdParkingZone(), request));
    }

    @Override public ResponseEntity createBooking(Principal principal, String idSlot, String fcm)
        throws JSONException {
        Customer customer = customerRepository.findByEmail(principal.getName());
        ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(idSlot);
        if (null != customer && null != parkingSlot && SLOT_SCAN_ME
            .equals(parkingSlot.getStatus())) {
            User user = userRepository.findByEmail(customer.getEmail());
            if (1 > bookingRepository.countAllByDateOutAndIdUser(null, customer.getIdCustomer())
                && !user.getRole().equals(CUSTOMER_BANNED)) {
                FcmClient fcmClient;
                fcmClient = new FcmClient();
                fcmClient.sendPushNotification(fcm, "", "");

                System.out.println(fcm);
                parkingSlot.setStatus(SLOT_TAKEN);
                parkingSlotRepository.save(parkingSlot);
                setupParkingLayout(parkingSlot, SLOT_TAKEN);
                ParkingZone parkingZone = parkingZoneRepository
                    .findParkingZoneByIdParkingZone(parkingSlot.getIdParkingZone());
                Booking bookingParking = new Booking();
                bookingParking.setParkingZoneName(parkingZone.getName());
                bookingParking.setAddress(parkingZone.getAddress());
                bookingParking.setPrice(parkingZone.getPrice());
                bookingParking.setImageUrl(parkingZone.getImageUrl());
                bookingParking.setIdParkingZone(parkingSlot.getIdParkingZone());
                bookingParking.setSlotName(parkingSlot.getName());
                bookingParking.setLevelName(
                    parkingLevelRepository.findByIdLevel(parkingSlot.getIdLevel()).getLevelName());
                bookingParking.setIdUser(customer.getIdCustomer());
                bookingParking.setCustomerName(customer.getName());
                bookingParking.setCustomerPhone(customer.getPhoneNumber());
                bookingParking.setIdSlot(idSlot);
                bookingParking.setDateIn(Calendar.getInstance().getTimeInMillis());
                bookingRepository.save(bookingParking);
                return ResponseEntity.ok().body(bookingParking);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override public ResponseEntity bookingReceipt(String idBooking) {
        Booking booking = bookingRepository.findBookingByIdBooking(idBooking);
        ParkingZone parkingZone =
            parkingZoneRepository.findParkingZoneByIdParkingZone(booking.getIdParkingZone());
        Receipt receipt = new Receipt();
        receipt.setCustomerName(booking.getCustomerName());
        receipt.setIdBooking(booking.getIdBooking());
        receipt.setParkingZoneName(parkingZone.getName());
        receipt.setAddress(parkingZone.getAddress());
        receipt.setSlotName(booking.getSlotName());
        receipt.setPrice(booking.getPrice());
        if (booking.getTotalTime() == null) {
            booking.setTotalTime(Long.toString(
                getTotalTime(booking.getDateIn(), Calendar.getInstance().getTimeInMillis())));
            booking.setTotalPrice(getTotalPrice(getTotalMinute(booking.getTotalTime()),
                getTotalHours(booking.getTotalTime(), getTotalMinute(booking.getTotalTime())),
                booking.getPrice()));
            booking.setDateOut(Calendar.getInstance().getTimeInMillis());
            receipt.setStatus("Ongoing");
        }
        receipt.setTotalMinutes(getTotalMinute(booking.getTotalTime()));
        receipt.setTotalHours(getTotalHours(booking.getTotalTime(), receipt.getTotalMinutes()));
        receipt.setDateIn(booking.getDateIn());
        receipt.setDateOut(booking.getDateOut());
        receipt.setTotalPrice(booking.getTotalPrice());
        return ResponseEntity.ok().body(receipt);
    }

    private Integer getTotalMinute(String totalTime) {
        return (Integer.parseInt(totalTime) % 60) + 1;
    }

    private Integer getTotalHours(String totalTime, int totalMinutes) {
        return (Integer.parseInt(totalTime) - totalMinutes + 1) / 60;
    }

    private String getTotalPrice(int totalMinutes, int totalHours, Double price) {
        if (totalMinutes != 0)
            totalHours += 1;
        return (String.valueOf(totalHours * price)).split("\\.")[0];
    }

    @Override public ResponseEntity checkoutBooking(Principal principal) {
        Customer customer = customerRepository.findByEmail(principal.getName());
        return checkoutBooking(customer);
    }

    @Override public ResponseEntity checkoutBookingSA(String id) {
        Booking booking = bookingRepository.findBookingByIdBooking(id);
        Customer customer = customerRepository.findByIdCustomer(booking.getIdUser());
        return checkoutBooking(customer);
    }

    private ResponseEntity checkoutBooking(Customer customer) {
        Booking bookingExist = bookingRepository.findBookingByIdBooking(
            bookingRepository.findBookingByIdUserAndDateOut(customer.getIdCustomer(), null)
                .getIdBooking());
        if (null != bookingExist) {
            ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(bookingExist.getIdSlot());
            if (SLOT_TAKEN.equals(parkingSlot.getStatus()) || SLOT_TAKEN
                .equals(parkingSlot.getStatus().substring(parkingSlot.getStatus().length() - 1))) {
                bookingCheckoutSetup(bookingExist, parkingSlot, parkingSlotRepository,
                    bookingRepository);
                setupParkingLayout(parkingSlot, SLOT_EMPTY);
                return ResponseEntity.ok().body(bookingExist);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    void bookingCheckoutSetup(Booking bookingExist, ParkingSlot parkingSlot,
        ParkingSlotRepository parkingSlotRepository, BookingRepository bookingRepository) {
        bookingExist.setDateOut(Calendar.getInstance().getTimeInMillis());
        bookingExist.setTotalTime(
            Long.toString(getTotalTime(bookingExist.getDateIn(), bookingExist.getDateOut())));
        bookingExist.setTotalPrice(getTotalPrice(getTotalMinute(bookingExist.getTotalTime()),
            getTotalHours(bookingExist.getTotalTime(), getTotalMinute(bookingExist.getTotalTime())),
            bookingExist.getPrice()));
        parkingSlot.setStatus(SLOT_EMPTY);
        parkingSlotRepository.save(parkingSlot);
        bookingRepository.save(bookingExist);
    }

    private void setupParkingLayout(ParkingSlot parkingSlot, String slotEmpty) {
        GenerateQRServiceImpl.SetSlotsLayout(slotEmpty, parkingSlot, parkingLevelRepository);
    }

    @Override public ResponseEntity findBookingById(String id) {
        return ResponseEntity.ok(bookingRepository.findBookingByIdBooking(id));
    }
}
