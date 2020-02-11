package com.future.pms.service.impl;

import com.future.pms.AmazonClient;
import com.future.pms.FcmClient;
import com.future.pms.model.*;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.*;
import com.future.pms.service.BookingService;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    @Autowired AmazonClient amazonClient;


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
                .findBookingByIdUserAndTotalPriceNotNull(customer.getIdCustomer(), request));
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override public ResponseEntity findOngoingBookingCustomer(Principal principal) {
        Customer customer = customerRepository.findByEmail(principal.getName());
        if (customer != null) {
            return ResponseEntity.ok(bookingRepository
                .findBookingByIdUserAndTotalPrice(customer.getIdCustomer(), null));
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
                ParkingZone parkingZone = parkingZoneRepository
                    .findParkingZoneByIdParkingZone(parkingSlot.getIdParkingZone());
                parkingSlot.setStatus(SLOT_TAKEN);
                parkingSlotRepository.save(parkingSlot);

                ParkingLevel parkingLevel =
                    parkingLevelRepository.findByIdLevel(parkingSlot.getIdLevel());
                FcmClient fcmClient;
                fcmClient = new FcmClient();
                fcmClient.sendPushNotification(fcm, customer.getName(), parkingZone.getName(),
                    parkingLevel.getLevelName());

                System.out.println(fcm);
                setupParkingLayout(parkingSlot, SLOT_TAKEN);
                Booking bookingParking = new Booking();
                bookingParking.setParkingZoneName(parkingZone.getName());
                bookingParking.setAddress(parkingZone.getAddress());
                bookingParking.setPrice(parkingZone.getPrice());
                bookingParking.setImageUrl(parkingZone.getImageUrl());
                bookingParking.setIdParkingZone(parkingSlot.getIdParkingZone());
                bookingParking.setSlotName(parkingSlot.getName());
                bookingParking.setLevelName(parkingLevel.getLevelName());
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
        receipt.setStatus("Completed");
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

    @Override public ResponseEntity checkoutBookingStepOne(Principal principal, String fcmToken)
        throws IOException {
        Customer customer = customerRepository.findByEmail(principal.getName());
        String filename;
        QR qr = new QR();
        qr.setIdSlot(customer.getIdCustomer());
        ByteArrayOutputStream bout =
            QRCode.from(qr + fcmToken).withSize(250, 250).to(ImageType.PNG).stream();
        filename = customer.getIdCustomer() + ".png";
        filename = amazonClient.convertMultiPartToFileQR(bout, filename);
        Booking bookingExist =
            bookingRepository.findBookingByIdUserAndDateOut(customer.getIdCustomer(), null);
        if (bookingExist != null) {
            return new ResponseEntity<>(filename, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override public ResponseEntity checkoutBookingSA(String id) {
        Booking booking = bookingRepository.findBookingByIdBooking(id);
        Customer customer = customerRepository.findByIdCustomer(booking.getIdUser());
        checkoutBooking(customer);
        return ResponseEntity.ok().body(booking);
    }

    private void checkoutBooking(Customer customer) {
        Booking ongoingBooking =
            bookingRepository.findBookingByIdUserAndTotalPrice(customer.getIdCustomer(), null);
        if (null != ongoingBooking) {
            ParkingSlot parkingSlot =
                parkingSlotRepository.findByIdSlot(ongoingBooking.getIdSlot());
            if (SLOT_TAKEN.equals(parkingSlot.getStatus())) {
                bookingCheckoutSetup(ongoingBooking, parkingSlot, parkingSlotRepository,
                    bookingRepository);
                setupParkingLayout(parkingSlot, SLOT_EMPTY);
            }
        }
    }

    void bookingCheckoutSetup(Booking bookingExist, ParkingSlot parkingSlot,
        ParkingSlotRepository parkingSlotRepository, BookingRepository bookingRepository) {
        if (bookingExist.getDateOut() == null) {
            bookingExist.setDateOut(Calendar.getInstance().getTimeInMillis());
        }
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

    @Override public ResponseEntity checkoutBookingStepTwo(Principal principal, String fcmToken,
        String idCustomer) throws JSONException {
        ParkingZone parkingZone =
            parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName());
        Customer customer = customerRepository.findByIdCustomer(idCustomer);
        Booking bookingExist = bookingRepository.findBookingByIdUserAndTotalPrice(idCustomer, null);
        if (bookingExist != null && parkingZone.getIdParkingZone()
            .equals(bookingExist.getIdParkingZone())) {
            checkoutBooking(customer);
            String bookingId = bookingExist.getIdBooking();
            bookingExist = bookingRepository.findBookingByIdBooking(bookingId);
            if (fcmToken != null) {
                FcmClient fcmClient;
                fcmClient = new FcmClient();
                fcmClient.sendPushNotificationCheckoutBooking(fcmToken,
                    bookingExist.getParkingZoneName(), bookingExist.getIdBooking());
            }
            return ResponseEntity.ok().body(bookingExist);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
