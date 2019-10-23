package com.future.pms.controller;

import com.future.pms.model.QR;
import com.future.pms.service.GenerateQRService;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.core.vcard.VCard;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Random;

@RestController
@RequestMapping("api/qr")
public class GenerateQRController {
    @Autowired
    GenerateQRService generateQRService;

    @GetMapping
    private ResponseEntity generateQr(String emailParkingZone) {
        return generateQRService.generateQR(emailParkingZone);
    }
}
