package com.future.pms.service;

import com.future.pms.model.parking.ParkingSlot;
import org.springframework.http.ResponseEntity;

public interface GenerateQRService {
    ResponseEntity generateQR(String emailParkingZone);
}
