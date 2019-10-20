package com.future.pms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String idUser;
    private String name;
    private String email;
    private String phoneNumber = "";
    private String password;
    private String role = "";
    private String imageUrl = "";
    private List<String>listHistory = new ArrayList<>();
}
