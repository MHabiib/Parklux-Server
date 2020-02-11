package com.future.pms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.pms.config.MongoTokenStore;
import com.future.pms.model.Booking;
import com.future.pms.model.Customer;
import com.future.pms.model.User;
import com.future.pms.model.request.CreateCustomerRequest;
import com.future.pms.model.request.UpdateCustomerRequest;
import com.future.pms.repository.BookingRepository;
import com.future.pms.repository.CustomerRepository;
import com.future.pms.repository.UserRepository;
import com.future.pms.service.BookingService;
import com.future.pms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;

import static com.future.pms.Constants.CUSTOMER;
import static com.future.pms.Constants.CUSTOMER_BANNED;

@Service public class CustomerServiceImpl implements CustomerService {
    @Autowired CustomerRepository customerRepository;
    @Autowired UserRepository userRepository;
    @Autowired BookingRepository bookingRepository;
    @Autowired BookingService bookingService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired MongoTokenStore mongoTokenStore;

    @Override public ResponseEntity loadAll(Integer page, String name) {
        PageRequest request = PageRequest.of(page, 10, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity
            .ok(customerRepository.findCustomerByNameContainingAllIgnoreCase(request, name));
    }

    @Override public ResponseEntity getUserDetail(Principal principal) {
        if (customerRepository.findByEmail(principal.getName()) != null) {
            return new ResponseEntity<>(customerRepository.findByEmail(principal.getName()),
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override public ResponseEntity updateCustomerSA(String id, String customerJson)
        throws IOException {
        UpdateCustomerRequest updatedCustomer =
            new ObjectMapper().readValue(customerJson, UpdateCustomerRequest.class);
        Customer customer = customerRepository.findByIdCustomer(id);
        User user = userRepository.findByEmail(customer.getEmail());
        return updateCustomer(updatedCustomer, customer, user);
    }

    private ResponseEntity updateCustomer(UpdateCustomerRequest updatedCustomer, Customer customer,
        User user) {
        if (null == customer || null == user)
            return new ResponseEntity<>("Error update customer", HttpStatus.BAD_REQUEST);
        customer.setName(updatedCustomer.getName());
        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        if (!customer.getEmail().equals(updatedCustomer.getEmail())) {
            if (!customer.getEmail().equals(updatedCustomer.getEmail())
                && userRepository.countByEmail(updatedCustomer.getEmail()) > 0) {
                return new ResponseEntity<>("Error update customer", HttpStatus.BAD_REQUEST);
            } else {
                mongoTokenStore.revokeToken(customer.getEmail());
                customer.setEmail(updatedCustomer.getEmail());
            }
        }
        user.setEmail(updatedCustomer.getEmail());
        if (!"".equals(updatedCustomer.getPassword())) {
            user.setPassword(passwordEncoder.encode(updatedCustomer.getPassword()));
        }
        customerRepository.save(customer);
        userRepository.save(user);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @Override public ResponseEntity updateCustomer(Principal principal, String customerJson)
        throws IOException {
        UpdateCustomerRequest updatedCustomer =
            new ObjectMapper().readValue(customerJson, UpdateCustomerRequest.class);
        Customer customer = customerRepository.findByEmail(principal.getName());
        User user = userRepository.findByEmail(principal.getName());
        if (user == null || user.getRole().equals(CUSTOMER_BANNED)) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
        return updateCustomer(updatedCustomer, customer, user);
    }

    @Override public ResponseEntity createCustomer(CreateCustomerRequest createCustomerRequest) {
        if (null != userRepository.findByEmail(createCustomerRequest.getEmail()))
            return new ResponseEntity<>("Email already registered", HttpStatus.BAD_REQUEST);
        Customer customer = new Customer();
        User user = new User();
        customer.setName(createCustomerRequest.getName());
        customer.setPhoneNumber(createCustomerRequest.getPhoneNumber());
        customer.setEmail(createCustomerRequest.getEmail());
        user.setEmail(createCustomerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(createCustomerRequest.getPassword()));
        user.setRole(CUSTOMER);
        customerRepository.save(customer);
        userRepository.save(user);
        return new ResponseEntity<>("Customer Created", HttpStatus.OK);
    }

    @Override public ResponseEntity getUserDetailSA(String id) {
        Customer customer = customerRepository.findByIdCustomer(id);
        if (customer != null) {
            return new ResponseEntity<>(customerRepository.findByIdCustomer(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override public ResponseEntity banCustomer(String id) {
        Booking booking = bookingRepository.findBookingByIdUserAndTotalPrice(id, null);
        if (booking != null) {
            bookingService.checkoutBookingSA(booking.getIdBooking());
        }
        Customer customer = customerRepository.findByIdCustomer(id);
        User user = userRepository.findByEmail(customer.getEmail());
        if (user.getRole().equals(CUSTOMER)) {
            user.setRole(CUSTOMER_BANNED);
            customer.setName(customer.getName() + " (BANNED)");
        } else {
            customer.setName(customer.getName().substring(0, customer.getName().length() - 9));
            user.setRole(CUSTOMER);
        }
        customerRepository.save(customer);
        userRepository.save(user);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
