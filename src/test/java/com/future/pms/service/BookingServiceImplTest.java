package com.future.pms.service;

import com.future.pms.model.Booking;
import com.future.pms.model.Customer;
import com.future.pms.model.User;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.*;
import com.future.pms.service.impl.BookingServiceImpl;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.future.pms.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.Silent.class) public class BookingServiceImplTest {
    private static final Booking BOOKING =
        Booking.builder().idBooking("idBooking").address("address").customerName("customerName")
            .customerPhone("customerPhone").dateIn(8L).dateOut(9L).idParkingZone("idParkingZone")
            .idSlot("idSlot").idUser("isUser").imageUrl("imageUrl").levelName("levelName")
            .parkingZoneName("parkingZoneName").price(100D).slotName("slotName").totalPrice("1000")
            .totalTime(null).build();
    private static final Customer CUSTOMER =
        Customer.builder().idCustomer("idCustomer").email("email").name("name")
            .phoneNumber("phoneNumber").build();
    private static final ParkingZone PARKING_ZONE =
        ParkingZone.builder().idParkingZone("idParkingZone").address("address")
            .emailAdmin("emailAdmin").imageUrl("imageUrl").name("name").phoneNumber("phoneNumber")
            .build();
    private static final ParkingSlot PARKING_SLOT =
        ParkingSlot.builder().idSlot("idSlot").idLevel("idLevel").idParkingZone("idParkingZone")
            .name("name").slotNumberInLayout(1).status(SLOT_TAKEN).status(SLOT_SCAN_ME).build();
    private static final ParkingLevel PARKING_LEVEL = ParkingLevel.builder().idLevel("idLevel")
        .slotsLayout(new ArrayList<>(Arrays.asList("_1", "_2"))).build();

    private static final User USER = User.builder().idUser("idUser").role(ADMIN).build();
    private static final List<Booking> LIST_OF_BOOKING = Collections.singletonList(BOOKING);
    private static final Page<Booking> PAGE_OF_BOOKING = new PageImpl<>(LIST_OF_BOOKING);
    private static final Pageable PAGEABLE =
        new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "dateIn"));
    private static String FILTER = "filter";
    private static String ID_SLOT = "05e22ea963516c80004e3efe80";

    @InjectMocks BookingServiceImpl bookingServiceImpl;
    @Mock BookingRepository bookingRepository;
    @Mock CustomerRepository customerRepository;
    @Mock ParkingZoneRepository parkingZoneRepository;
    @Mock ParkingSlotRepository parkingSlotRepository;
    @Mock UserRepository userRepository;
    @Mock ParkingLevelRepository parkingLevelRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthorizationServerTokenServices authorizationServerTokenServices;
    @Mock ConsumerTokenServices consumerTokenServices;
    @Mock private Principal principal;

    @Test public void loadAllAll() {
        Mockito.when(bookingRepository.findBookingBy(PAGEABLE)).thenReturn(PAGE_OF_BOOKING);

        ResponseEntity responseEntity = bookingServiceImpl.loadAll(ALL, 0);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(bookingRepository).findBookingBy(PAGEABLE);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test public void loadAllOngoing() {
        Mockito.when(bookingRepository.findBookingByDateOutNull(PAGEABLE))
            .thenReturn(PAGE_OF_BOOKING);

        ResponseEntity responseEntity = bookingServiceImpl.loadAll(ONGOING, 0);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(bookingRepository).findBookingByDateOutNull(PAGEABLE);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test public void loadAllDefault() {
        Mockito.when(bookingRepository.findBookingByDateOutNotNull(PAGEABLE))
            .thenReturn(PAGE_OF_BOOKING);

        ResponseEntity responseEntity = bookingServiceImpl.loadAll(FILTER, 0);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(bookingRepository).findBookingByDateOutNotNull(PAGEABLE);
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test public void findBookingCustomerNotNull() {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);

        ResponseEntity responseEntity = bookingServiceImpl.findBookingCustomer(principal, 0);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void findBookingCustomerNull() {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(null);

        ResponseEntity responseEntity = bookingServiceImpl.findBookingCustomer(principal, 0);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void findOngoingBookingCustomerNotNull() {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);

        ResponseEntity responseEntity = bookingServiceImpl.findOngoingBookingCustomer(principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void findOngoingBookingCustomerNull() {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(null);

        ResponseEntity responseEntity = bookingServiceImpl.findOngoingBookingCustomer(principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void findOngoingBookingParkingZone() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity =
            bookingServiceImpl.findOngoingBookingParkingZone(principal, 0);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void findPastBookingParkingZone() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = bookingServiceImpl.findPastBookingParkingZone(principal, 0);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void createBookingFailed() throws JSONException {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(null);
        Mockito.when(parkingSlotRepository.findByIdSlot(ID_SLOT)).thenReturn(null);

        ResponseEntity responseEntity = bookingServiceImpl.createBooking(principal, ID_SLOT, "");

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void createBookingSuccess() throws JSONException {
        PARKING_SLOT.setStatus(SLOT_SCAN_ME);
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito.when(parkingSlotRepository.findByIdSlot(ID_SLOT))
            .thenReturn(PARKING_SLOT);
        Mockito.when(userRepository.findByEmail(CUSTOMER.getEmail())).thenReturn(USER);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(
            parkingZoneRepository.findParkingZoneByIdParkingZone(PARKING_SLOT.getIdParkingZone()))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = bookingServiceImpl.createBooking(principal, ID_SLOT, "");

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void bookingReceipt() {
        Mockito
            .when(parkingZoneRepository.findParkingZoneByIdParkingZone(BOOKING.getIdParkingZone()))
            .thenReturn(PARKING_ZONE);
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);

        ResponseEntity responseEntity = bookingServiceImpl.bookingReceipt(BOOKING.getIdBooking());

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository)
            .findParkingZoneByIdParkingZone(BOOKING.getIdParkingZone());
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void checkoutBooking() {
        PARKING_SLOT.setStatus(SLOT_TAKEN);
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito
            .when(bookingRepository.findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null))
            .thenReturn(BOOKING);
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = bookingServiceImpl.checkoutBooking(principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void checkoutBookingNotSlotTaken() {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito
            .when(bookingRepository.findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null))
            .thenReturn(BOOKING);
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = bookingServiceImpl.checkoutBooking(principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void checkoutBookingFailed() {
        PARKING_SLOT.setStatus(SLOT_TAKEN);
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito
            .when(bookingRepository.findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null))
            .thenReturn(BOOKING);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = bookingServiceImpl.checkoutBooking(principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void checkoutBookingSA() {
        PARKING_SLOT.setStatus(SLOT_TAKEN);
        Mockito.when(customerRepository.findByIdCustomer(BOOKING.getIdUser())).thenReturn(CUSTOMER);
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);
        Mockito
            .when(bookingRepository.findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null))
            .thenReturn(BOOKING);
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity =
            bookingServiceImpl.checkoutBookingSA(BOOKING.getIdBooking());

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByIdCustomer(BOOKING.getIdUser());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void findBookingById() {
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);

        ResponseEntity responseEntity = bookingServiceImpl.findBookingById(BOOKING.getIdBooking());

        assertThat(responseEntity).isNotNull();

        Mockito.verify(bookingRepository).findBookingByIdBooking(BOOKING.getIdBooking());
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }
}
