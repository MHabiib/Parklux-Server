package com.future.pms.model.list;

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
public class ParkingZoneLevel {
    @Id
    private String idLevel;
    private String parkingZoneName;
    private String levelName;
    private List<ParkingZoneSection> parkingZoneSections = new ArrayList<>();
}
