package com.future.pms.controller;

import com.future.pms.model.request.CreateCustomerRequest;
import com.future.pms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@CrossOrigin("**") @RestController @RequestMapping
public class CustomerController {
    @Autowired CustomerService customerService;

    @GetMapping("/api/customer") public ResponseEntity loadAll(Integer page) {
        return ResponseEntity.ok(customerService.loadAll(page));
    }

    @GetMapping("/api/customer/detail") public ResponseEntity getUserDetail(Principal principal) {
        return ResponseEntity.ok(customerService.getUserDetail(principal));
    }

    @PutMapping(value = "api/customer/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCustomer(Principal principal,
        @RequestPart("customer") String customerJson) throws IOException {
        return customerService.updateCustomer(principal, customerJson);
    }

    @PostMapping("/customer/create")
    public ResponseEntity createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        return customerService.createCustomer(createCustomerRequest);
    }
}
