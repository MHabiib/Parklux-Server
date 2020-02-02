package com.future.pms.controller;

import com.future.pms.model.request.CreateCustomerRequest;
import com.future.pms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@CrossOrigin("**") @RestController @RequestMapping public class CustomerController {
    @Autowired CustomerService customerService;

    @GetMapping("/api3/customer") public ResponseEntity loadAll(Integer page, String name) {
        return ResponseEntity.ok(customerService.loadAll(page, name));
    }

    @GetMapping("/api/customer/detail") public ResponseEntity getUserDetail(Principal principal) {
        return customerService.getUserDetail(principal);
    }

    @GetMapping("/api3/customer/{id}/detail")
    public ResponseEntity getUserDetailSA(@PathVariable("id") String id) {
        return ResponseEntity.ok(customerService.getUserDetailSA(id));
    }

    @PutMapping(value = "api/customer/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCustomer(Principal principal,
        @RequestPart("customer") String customerJson) throws IOException {
        return customerService.updateCustomer(principal, customerJson);
    }

    @PutMapping(value = "api3/customer/{id}/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCustomerSA(@PathVariable("id") String id,
        @RequestPart("customer") String customerJson) throws IOException {
        return customerService.updateCustomerSA(id, customerJson);
    }

    @PostMapping("/customer/create")
    public ResponseEntity createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        return customerService.createCustomer(createCustomerRequest);
    }

    @PostMapping("/api3/{id}/customer/ban")
    public ResponseEntity banCustomer(@PathVariable("id") String id) {
        return customerService.banCustomer(id);
    }
}
