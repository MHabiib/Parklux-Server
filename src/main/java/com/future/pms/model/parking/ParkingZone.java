package com.future.pms.model.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.future.pms.Constants.OPEN_HOUR;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingZone {
    private String name = "";
    private String emailParkingZone;
    private Double price = 0.0;
    private String openHour = OPEN_HOUR;
    private String address = "";
    private String phoneNumber = "";
    private String imageUrl = "";
}

