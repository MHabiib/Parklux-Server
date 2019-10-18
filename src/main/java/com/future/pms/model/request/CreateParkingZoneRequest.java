package com.future.pms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateParkingZoneRequest {
    private String name;
    private String address;
    private Double price;
    private String openHour;
}
