package com.future.pms.model;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {
    @Id
    private String idBooking;
    private String parkingZoneName;
    private String address;
    private String slotName;
    private Double price;
    private Integer totalHours;
    private Integer totalMinutes;
    private Long dateIn;
    private Long dateOut;
    private String totalPrice;
}