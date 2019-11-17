package com.future.pms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor public class UpdateCustomerRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}
