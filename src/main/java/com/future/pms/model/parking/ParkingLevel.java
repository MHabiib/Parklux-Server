package com.future.pms.model.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

@Data @Builder @AllArgsConstructor @NoArgsConstructor public class ParkingLevel {
    @Id private String idLevel;
    private String idParkingZone;
    private String levelName;
    private String status;
    private ArrayList<String> slotsLayout;
}
