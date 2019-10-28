package com.future.pms.model.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import static com.future.pms.Constants.AVAILABLE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLevel {
    @Id
    private String idLevel;
    private String emailParkingZone;
    private String levelName;
    private String status = AVAILABLE;
}
