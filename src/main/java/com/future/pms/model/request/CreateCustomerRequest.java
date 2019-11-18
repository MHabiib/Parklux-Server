package com.future.pms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder public class CreateCustomerRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}
