package com.future.pms.model.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

@Data @Builder @AllArgsConstructor @NoArgsConstructor public class ParkingSlot {
    @Version Long version;
    @Id private String idSlot;
    private String idParkingZone;
    private String idLevel;
    private String name;
    private String status;
    private Integer slotNumberInLayout;
}
