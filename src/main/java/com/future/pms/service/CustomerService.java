package com.future.pms.service;

import com.future.pms.model.request.CreateCustomerRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.Principal;

public interface CustomerService {
    ResponseEntity loadAll(Integer page, String name);

    ResponseEntity getUserDetail(Principal principal);

    ResponseEntity updateCustomer(Principal principal, String customerJson) throws IOException;

    ResponseEntity updateCustomerSA(String id, String customerJson) throws IOException;

    ResponseEntity createCustomer(CreateCustomerRequest createCustomerRequest);

    ResponseEntity getUserDetailSA(String id);

    ResponseEntity banCustomer(String id);
}
