package com.future.pms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.pms.Constants;
import com.future.pms.config.MongoTokenStore;
import com.future.pms.model.Booking;
import com.future.pms.model.Customer;
import com.future.pms.model.User;
import com.future.pms.model.request.CreateCustomerRequest;
import com.future.pms.model.request.UpdateCustomerRequest;
import com.future.pms.repository.BookingRepository;
import com.future.pms.repository.CustomerRepository;
import com.future.pms.repository.UserRepository;
import com.future.pms.service.impl.CustomerServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.Silent.class) public class CustomerServiceImplTest {

    private static final Customer CUSTOMER =
        Customer.builder().idCustomer("idCustomer").email("email").name("name")
            .phoneNumber("phoneNumber").build();
    private static final CreateCustomerRequest CREATE_CUSTOMER_REQUEST =
        CreateCustomerRequest.builder().name("name").email("email").build();
    private static final User USER =
        User.builder().idUser("idUser").email(null).password("passwordUser").role("roleUser")
            .build();
    private static final Booking BOOKING = Booking.builder().idBooking("idBooking").build();
    private static final String CUSTOMER_JSON = "{\n" + "    \"name\": \"Ryujin (Android 2)\",\n"
        + "    \"email\": \"android2@mail.com\",\n" + "    \"phoneNumber\": \"14022\",\n"
        + "    \"password\": \"$2a$10$QBqnrD1L9SJ4dZ6DgLcxTO.bSvli2ujCW1jk2nSIEpqFGz0aPqOYi\"\n"
        + "}";
    private static final UpdateCustomerRequest UPDATE_CUSTOMER_REQUEST =
        UpdateCustomerRequest.builder().build();
    private static final Pageable PAGEABLE =
        new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "name"));
    private static final List<Customer> LIST_OF_CUSTOMER = Collections.singletonList(CUSTOMER);
    private static final Page<Customer> PAGE_OF_CUSTOMER = new PageImpl<>(LIST_OF_CUSTOMER);

    @InjectMocks CustomerServiceImpl customerServiceImpl;
    @Mock BookingService bookingService;
    @Mock CustomerRepository customerRepository;
    @Mock BookingRepository bookingRepository;
    @Mock Principal principal;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock MongoTokenStore mongoTokenStore;
    @Spy ObjectMapper objectMapper;

    @Test public void loadAll() {
        Mockito.when(customerRepository
            .findCustomerByNameContainingAllIgnoreCase(PAGEABLE, CUSTOMER.getName()))
            .thenReturn(PAGE_OF_CUSTOMER);

        ResponseEntity responseEntity = customerServiceImpl.loadAll(0, CUSTOMER.getName());

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository)
            .findCustomerByNameContainingAllIgnoreCase(PAGEABLE, CUSTOMER.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void getUserDetailUserNotFound() {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(null);

        ResponseEntity responseEntity = customerServiceImpl.getUserDetail(principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void getUserDetailUserFound() {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);

        ResponseEntity responseEntity = customerServiceImpl.getUserDetail(principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(customerRepository, Mockito.times(2)).findByEmail(principal.getName());
        Mockito.verifyNoMoreInteractions(customerRepository);
    }

    @Test public void updateCustomerSA() throws IOException {
        Mockito.when(objectMapper.readValue(CUSTOMER_JSON, UpdateCustomerRequest.class))
            .thenReturn(UPDATE_CUSTOMER_REQUEST);
        Mockito.when(customerRepository.findByIdCustomer(CUSTOMER.getIdCustomer()))
            .thenReturn(CUSTOMER);
        Mockito.when(userRepository.findByEmail(CUSTOMER.getEmail())).thenReturn(USER);
        Mockito.when(userRepository.countByEmail("android2@mail.com")).thenReturn(2);

        ResponseEntity responseEntity =
            customerServiceImpl.updateCustomerSA(CUSTOMER.getIdCustomer(), CUSTOMER_JSON);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateCustomerSAFailed() throws IOException {
        Mockito.when(objectMapper.readValue(CUSTOMER_JSON, UpdateCustomerRequest.class))
            .thenReturn(UPDATE_CUSTOMER_REQUEST);
        Mockito.when(customerRepository.findByIdCustomer(CUSTOMER.getIdCustomer()))
            .thenReturn(CUSTOMER);
        Mockito.when(userRepository.findByEmail(CUSTOMER.getEmail())).thenReturn(null);

        ResponseEntity responseEntity =
            customerServiceImpl.updateCustomerSA(CUSTOMER.getIdCustomer(), CUSTOMER_JSON);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateCustomerFailedEmailAlreadyRegistered() throws IOException {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(USER);

        ResponseEntity responseEntity =
            customerServiceImpl.updateCustomer(principal, CUSTOMER_JSON);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateCustomer() throws IOException {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(USER);

        ResponseEntity responseEntity =
            customerServiceImpl.updateCustomer(principal, CUSTOMER_JSON);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateCustomerFailed() throws IOException {
        Mockito.when(customerRepository.findByEmail(principal.getName())).thenReturn(CUSTOMER);
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(null);

        ResponseEntity responseEntity =
            customerServiceImpl.updateCustomer(principal, CUSTOMER_JSON);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void createCustomerEmailAlreadyRegistered() {
        USER.setEmail("email");
        Mockito.when(userRepository.findByEmail(USER.getEmail())).thenReturn(USER);

        ResponseEntity responseEntity = customerServiceImpl.createCustomer(CREATE_CUSTOMER_REQUEST);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void createCustomerEmailSuccess() {
        Mockito.when(userRepository.findByEmail(USER.getEmail())).thenReturn(null);

        ResponseEntity responseEntity = customerServiceImpl.createCustomer(CREATE_CUSTOMER_REQUEST);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getUserDetailSASuccess() {
        Mockito.when(customerRepository.findByIdCustomer(CUSTOMER.getIdCustomer()))
            .thenReturn(CUSTOMER);

        ResponseEntity responseEntity =
            customerServiceImpl.getUserDetailSA(CUSTOMER.getIdCustomer());

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getUserDetailSAFailed() {
        Mockito.when(customerRepository.findByIdCustomer(CUSTOMER.getIdCustomer()))
            .thenReturn(null);

        ResponseEntity responseEntity =
            customerServiceImpl.getUserDetailSA(CUSTOMER.getIdCustomer());

        assertThat(responseEntity).isNotNull();
    }

    @Test public void banCustomerCustomer() {
        CUSTOMER.setEmail("email");
        USER.setRole(Constants.CUSTOMER);
        Mockito
            .when(bookingRepository.findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null))
            .thenReturn(BOOKING);
        Mockito.when(customerRepository.findByIdCustomer(CUSTOMER.getIdCustomer()))
            .thenReturn(CUSTOMER);
        Mockito.when(userRepository.findByEmail(CUSTOMER.getEmail())).thenReturn(USER);

        ResponseEntity responseEntity = customerServiceImpl.banCustomer(CUSTOMER.getIdCustomer());

        assertThat(responseEntity).isNotNull();
    }

    @Test public void banCustomerNotCustomer() {
        CUSTOMER.setEmail("email");
        Mockito
            .when(bookingRepository.findBookingByIdUserAndDateOut(CUSTOMER.getIdCustomer(), null))
            .thenReturn(BOOKING);
        Mockito.when(customerRepository.findByIdCustomer(CUSTOMER.getIdCustomer()))
            .thenReturn(CUSTOMER);
        Mockito.when(userRepository.findByEmail(CUSTOMER.getEmail())).thenReturn(USER);

        ResponseEntity responseEntity = customerServiceImpl.banCustomer(CUSTOMER.getIdCustomer());

        assertThat(responseEntity).isNotNull();
    }
}
