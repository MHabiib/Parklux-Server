package com.future.pms.model.list;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ParkingZoneSection {
    @Id
    private String idSection;
    private String parkingZoneName;
    private String levelName;
    private String sectionName;
    private List<ParkingZoneSlot> parkingZoneSlots;
}
