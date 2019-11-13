package com.future.pms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data @Builder @AllArgsConstructor @NoArgsConstructor public class User {
    @Id private String idUser;
    private String email;
    private String password;
    private String role;
}
