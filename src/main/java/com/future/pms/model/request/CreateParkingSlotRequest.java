package com.future.pms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder public class CreateParkingSlotRequest {
    private String idSection;
    private String slotName;
    private String idParkingZone;
}
