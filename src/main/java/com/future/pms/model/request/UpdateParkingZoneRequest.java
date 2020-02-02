package com.future.pms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor public class UpdateParkingZoneRequest {
    private String emailAdmin;
    private String password;
    private String name;
    private Double price;
    private String openHour;
    private String address;
    private String phoneNumber;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
}
