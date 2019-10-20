package com.future.pms.model.parking;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ParkingSection {
    @Id
    private String idSection;
    private String idLevel;
    private String idParkingZone;
    private String sectionName;
    private String status = "AVAILABLE";
}
