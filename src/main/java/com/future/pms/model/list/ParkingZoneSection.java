package com.future.pms.model.list;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ParkingZoneSection {
    private String name;
    private List<ParkingZoneSlot> parkingZoneSlots;
}
