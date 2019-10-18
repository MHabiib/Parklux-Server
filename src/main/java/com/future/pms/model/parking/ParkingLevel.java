package com.future.pms.model.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLevel {
    @Id
    private String idLevel;
    private String parkingZoneName;
    private String levelName;
    private String status;
}
