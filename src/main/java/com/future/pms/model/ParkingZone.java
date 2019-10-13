package com.future.pms.model;

import com.future.pms.model.list.ParkingZoneLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private String openHour = "--:-- / --:--";
    private List<ParkingZoneLevel> parkingZoneLevels = new ArrayList<>();
}
