package com.future.pms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data @Builder @AllArgsConstructor @NoArgsConstructor public class Booking {
    @Id private String idBooking;
    private String idSlot;
    private String idUser;
    private String idParkingZone;
    private String customerName;
    private String customerPhone;
    private String parkingZoneName;
    private String address;
    private Double price;
    private String totalPrice;
    private String slotName;
    private String levelName;
    private String totalTime;
    private Long dateIn;
    private Long dateOut;
    private String imageUrl;
}
