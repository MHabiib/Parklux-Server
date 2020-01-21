package com.future.pms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.pms.model.Customer;
import com.future.pms.model.User;
import com.future.pms.model.request.UpdateCustomerRequest;
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
    private static final User USER =
        User.builder().idUser("idUser").email(null).password("passwordUser").role("roleUser")
            .build();
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
    @Mock CustomerRepository customerRepository;
    @Mock Principal principal;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
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

        ResponseEntity responseEntity =
            customerServiceImpl.updateCustomerSA(CUSTOMER.getIdCustomer(), CUSTOMER_JSON);

        assertThat(responseEntity).isNotNull();
    }
}
