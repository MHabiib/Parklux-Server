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
public class ParkingSlot {
    @Id
    private String idSlot;
    private String idSection;
    private String emailParkingZone;
    private String name;
    private String status = AVAILABLE;
}
