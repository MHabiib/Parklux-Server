package com.future.pms.controller;

import com.future.pms.service.GenerateQRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("api/qr") public class GenerateQRController {
    @Autowired GenerateQRService generateQRService;

    @GetMapping private ResponseEntity generateQr(String idParkingZone) {
        return generateQRService.generateQR(idParkingZone);
    }
}
