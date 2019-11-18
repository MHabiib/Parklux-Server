package com.future.pms.service;

import com.future.pms.model.request.CreateCustomerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;

import java.io.IOException;
import java.security.Principal;

public interface CustomerService {
    ResponseEntity getUserDetail(Principal principal);

    ResponseEntity updateCustomer(Principal principal, @RequestPart("customer") String customerJson)
        throws IOException;

    ResponseEntity createCustomer(CreateCustomerRequest createCustomerRequest);
}
