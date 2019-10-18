package com.future.pms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    private String idBooking;
    private String idUser;
    private String idParkingZone;
    private String qrUrl;
    private Long dateIn;
    private Long dateOut;
}
