package com.future.pms.controller;

import com.future.pms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@CrossOrigin("**") @RestController @RequestMapping("/api/customer")
public class CustomerController {
    @Autowired CustomerService customerService;

    @GetMapping("/detail") public ResponseEntity getUserDetail(Principal principal) {
        return ResponseEntity.ok(customerService.getUserDetail(principal));
    }

    @PutMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCustomer(Principal principal,
        @RequestPart("customer") String customerJson) throws IOException {
        return customerService.updateCustomer(principal, customerJson);
    }
}
