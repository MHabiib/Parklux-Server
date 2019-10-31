package com.future.pms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    private String idBooking;
    private String idSlot;
    private String idUser;
    private String idParkingZone;
    private Long dateIn;
    private Long dateOut;
}
