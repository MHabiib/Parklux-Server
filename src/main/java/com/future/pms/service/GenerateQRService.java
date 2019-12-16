package com.future.pms.service;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface GenerateQRService {
    ResponseEntity generateQR(String idParkingZone);

    ResponseEntity getImage(String imageName) throws IOException;
}
