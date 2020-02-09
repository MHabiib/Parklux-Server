package com.future.pms.service;

import com.future.pms.AmazonClient;
import com.future.pms.FcmClient;
import com.future.pms.model.Booking;
import com.future.pms.model.Customer;
import com.future.pms.model.User;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.*;
import com.future.pms.service.impl.BookingServiceImpl;
import org.joda.time.DateTimeUtils;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
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

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.future.pms.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.Silent.class) public class BookingServiceImplTest {
    private static final Booking BOOKING =
        Booking.builder().idBooking("idBooking").address("address").customerName("customerName")
            .customerPhone("customerPhone").dateIn(8L).dateOut(9L).idParkingZone("idParkingZone")
            .idSlot("idSlot").idUser("isUser").imageUrl("imageUrl").levelName("levelName")
            .parkingZoneName("parkingZoneName").price(100D).slotName("slotName").totalPrice("1000")
            .totalTime(null).build();
    private static final Booking BOOKING2 =
        Booking.builder().idBooking("idBooking").address("address").customerName("customerName")
            .customerPhone("customerPhone").dateIn(8L).dateOut(null).idParkingZone("idParkingZone")
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
    private static String FCM_TOKEN = "fcmToken";
    private static String ID_SLOT = "05e22ea963516c80004e3efe80";

    @InjectMocks BookingServiceImpl bookingServiceImpl;
    @Mock BookingRepository bookingRepository;
    @Mock CustomerRepository customerRepository;
    @Mock ParkingZoneRepository parkingZoneRepository;
    @Mock ParkingSlotRepository parkingSlotRepository;
    @Mock UserRepository userRepository;
    @Mock ParkingLevelRepository parkingLevelRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AmazonClient amazonClient;
    @Mock FcmClient fcmClient;
    @Mock AuthorizationServerTokenServices authorizationServerTokenServices;
    @Mock ConsumerTokenServices consumerTokenServices;
    @Mock private Principal principal;

    private SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

    @Before public void before() throws Exception {
        Date fixedDateTime = DATE_FORMATTER.parse("01/07/2016 16:45:00:000");
        DateTimeUtils.setCurrentMillisFixed(fixedDateTime.getTime());
    }

    @After public void after() {
        DateTimeUtils.setCurrentMillisSystem();
    }

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
        Mockito.verify(parkingSlotRepository).findByIdSlot(ID_SLOT);
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
    }

    @Test public void createBookingSuccess() throws JSONException {
        PARKING_SLOT.setStatus(SLOT_SCAN_ME);
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito.when(parkingSlotRepository.findByIdSlot(ID_SLOT)).thenReturn(PARKING_SLOT);
        Mockito.when(userRepository.findByEmail(CUSTOMER.getEmail())).thenReturn(USER);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(
            parkingZoneRepository.findParkingZoneByIdParkingZone(PARKING_SLOT.getIdParkingZone()))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = bookingServiceImpl.createBooking(principal, ID_SLOT, "");

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verify(parkingSlotRepository).findByIdSlot(ID_SLOT);
        Mockito.verify(parkingSlotRepository).save(PARKING_SLOT);
        Mockito.verify(userRepository).findByEmail(CUSTOMER.getEmail());
        Mockito.verify(parkingLevelRepository, Mockito.times(2))
            .findByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verify(parkingZoneRepository)
            .findParkingZoneByIdParkingZone(PARKING_SLOT.getIdParkingZone());
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
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
        Mockito.verify(bookingRepository).findBookingByIdBooking(BOOKING.getIdBooking());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void checkoutBooking() throws IOException {
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

        ResponseEntity responseEntity = bookingServiceImpl.checkoutBookingStepOne(principal, "");

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verify(bookingRepository)
            .findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null);
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void checkoutBookingNotSlotTaken() throws IOException {
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

        ResponseEntity responseEntity = bookingServiceImpl.checkoutBookingStepOne(principal, "");

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verify(bookingRepository)
            .findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null);
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void checkoutBookingFailed() throws IOException {
        PARKING_SLOT.setStatus(SLOT_TAKEN);
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito
            .when(bookingRepository.findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null))
            .thenReturn(BOOKING);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = bookingServiceImpl.checkoutBookingStepOne(principal, "");

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verify(bookingRepository)
            .findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null);
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void checkoutBookingFailedNull() throws IOException {
        PARKING_SLOT.setStatus(SLOT_TAKEN);
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito
            .when(bookingRepository.findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null))
            .thenReturn(null);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = bookingServiceImpl.checkoutBookingStepOne(principal, "");

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verify(bookingRepository)
            .findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null);
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void checkoutBookingSA() {
        PARKING_SLOT.setStatus(SLOT_TAKEN);
        Mockito.when(customerRepository.findByIdCustomer(BOOKING.getIdUser())).thenReturn(CUSTOMER);
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);
        Mockito.when(
            bookingRepository.findBookingByIdUserAndTotalPrice(CUSTOMER.getIdCustomer(), null))
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
        Mockito.verify(bookingRepository)
            .findBookingByIdUserAndTotalPrice(CUSTOMER.getIdCustomer(), null);
        Mockito.verify(bookingRepository).findBookingByIdBooking(BOOKING.getIdBooking());
        Mockito.verify(bookingRepository).save(BOOKING);
        Mockito.verify(parkingSlotRepository).findByIdSlot(BOOKING.getIdSlot());
        Mockito.verify(parkingSlotRepository).save(PARKING_SLOT);
        Mockito.verify(parkingLevelRepository).findByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void checkoutBookingSABookingDateoutNull() {
        PARKING_SLOT.setStatus(SLOT_TAKEN);
        Mockito.when(customerRepository.findByIdCustomer(BOOKING.getIdUser())).thenReturn(CUSTOMER);
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);
        Mockito.when(
            bookingRepository.findBookingByIdUserAndTotalPrice(CUSTOMER.getIdCustomer(), null))
            .thenReturn(BOOKING2);
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
        Mockito.verify(bookingRepository).findBookingByIdBooking(BOOKING.getIdBooking());
        Mockito.verify(bookingRepository)
            .findBookingByIdUserAndTotalPrice(CUSTOMER.getIdCustomer(), null);
        Mockito.verify(parkingSlotRepository).findByIdSlot(BOOKING.getIdSlot());
        Mockito.verify(parkingSlotRepository).save(PARKING_SLOT);
        Mockito.verify(parkingLevelRepository).findByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        //        Mockito.verify(bookingRepository).save(BOOKING); different timestamp
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void findBookingById() {
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);

        ResponseEntity responseEntity = bookingServiceImpl.findBookingById(BOOKING.getIdBooking());

        assertThat(responseEntity).isNotNull();

        Mockito.verify(bookingRepository).findBookingByIdBooking(BOOKING.getIdBooking());
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test public void checkoutBookingStepTwoBookingNotNull() throws JSONException {
        Mockito.when(customerRepository.findByIdCustomer(CUSTOMER.getIdCustomer()))
            .thenReturn(CUSTOMER);
        Mockito.when(
            bookingRepository.findBookingByIdUserAndTotalPrice(CUSTOMER.getIdCustomer(), null))
            .thenReturn(BOOKING);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT);
        Mockito.when(bookingRepository.findBookingByIdBooking(BOOKING.getIdBooking()))
            .thenReturn(BOOKING);

        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = bookingServiceImpl
            .checkoutBookingStepTwo(principal, FCM_TOKEN, CUSTOMER.getIdCustomer());

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByIdCustomer(CUSTOMER.getIdCustomer());
        Mockito.verify(parkingSlotRepository).findByIdSlot(BOOKING.getIdSlot());
        Mockito.verify(bookingRepository, Mockito.times(2))
            .findBookingByIdUserAndTotalPrice(CUSTOMER.getIdCustomer(), null);
        Mockito.verify(bookingRepository).findBookingByIdBooking(BOOKING.getIdBooking());
        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void checkoutBookingStepTwoBookingNull() throws JSONException {
        Mockito.when(customerRepository.findByIdCustomer(CUSTOMER.getIdCustomer()))
            .thenReturn(CUSTOMER);
        Mockito.when(
            bookingRepository.findBookingByIdUserAndTotalPrice(CUSTOMER.getIdCustomer(), null))
            .thenReturn(null);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT);

        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = bookingServiceImpl
            .checkoutBookingStepTwo(principal, FCM_TOKEN, CUSTOMER.getIdCustomer());

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByIdCustomer(CUSTOMER.getIdCustomer());
        Mockito.verify(bookingRepository)
            .findBookingByIdUserAndTotalPrice(CUSTOMER.getIdCustomer(), null);
        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }
}
