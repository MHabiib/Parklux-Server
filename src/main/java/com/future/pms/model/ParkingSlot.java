package com.future.pms.model;

import com.future.pms.model.list.ParkingZoneLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlot {
    @Id
    private String idParkingSlot;
    private List<ParkingZoneLevel> parkingZoneLevels = new ArrayList<>();
}
