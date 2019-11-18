package com.future.pms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.pms.model.Customer;
import com.future.pms.model.User;
import com.future.pms.model.request.CreateCustomerRequest;
import com.future.pms.model.request.UpdateCustomerRequest;
import com.future.pms.repository.CustomerRepository;
import com.future.pms.repository.UserRepository;
import com.future.pms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;

@Service public class CustomerServiceImpl implements CustomerService {
    @Autowired CustomerRepository customerRepository;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Override public ResponseEntity getUserDetail(Principal principal) {
        return ResponseEntity.ok(customerRepository.findByEmail(principal.getName()));
    }

    @Override public ResponseEntity updateCustomer(Principal principal, String customerJson)
        throws IOException {
        UpdateCustomerRequest updatedCustomer =
            new ObjectMapper().readValue(customerJson, UpdateCustomerRequest.class);
        Customer customer = customerRepository.findByEmail(principal.getName());
        User user = userRepository.findByEmail(principal.getName());
        if (null == customer || null == user)
            return new ResponseEntity<>("Error update customer", HttpStatus.BAD_REQUEST);
        customer.setName(updatedCustomer.getName());
        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        customer.setEmail(updatedCustomer.getEmail());
        if (null != updatedCustomer.getPassword() && null != updatedCustomer.getEmail()) {
            user.setPassword(passwordEncoder.encode(updatedCustomer.getPassword()));
            user.setEmail(updatedCustomer.getEmail());
        }
        customerRepository.save(customer);
        userRepository.save(user);
        return new ResponseEntity<>("Customer updated", HttpStatus.OK);
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
        user.setRole("CUSTOMER");
        customerRepository.save(customer);
        userRepository.save(user);
        return new ResponseEntity<>("Customer Created", HttpStatus.OK);
    }
}
