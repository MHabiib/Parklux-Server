package com.future.pms.model.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingZone {
    @Id
    private String idParkingZone;
    private String name;
    private String address = "";
    private Double price = 0.0;
    private String openHour = "-- : -- / -- : --";
}
