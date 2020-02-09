package com.future.pms.service;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.Principal;

public interface BookingService {
    ResponseEntity loadAll(String filter, Integer page);

    ResponseEntity findBookingCustomer(Principal principal, Integer page);

    ResponseEntity findOngoingBookingParkingZone(Principal principal, Integer page);

    ResponseEntity findPastBookingParkingZone(Principal principal, Integer page);

    ResponseEntity findOngoingBookingCustomer(Principal principal);

    ResponseEntity createBooking(Principal principal, String idSlot, String fcm)
        throws JSONException;

    ResponseEntity bookingReceipt(String id);

    ResponseEntity checkoutBookingStepOne(Principal principal, String fcmToken) throws IOException;

    ResponseEntity checkoutBookingSA(String id);

    ResponseEntity findBookingById(String id);

    ResponseEntity checkoutBookingStepTwo(Principal principal, String fcmToken, String idCustomer)
        throws JSONException;
}
