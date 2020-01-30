package com.future.pms.controller;

import com.future.pms.service.GenerateQRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;

@RestController @RequestMapping public class GenerateQRController {
    @Autowired GenerateQRService generateQRService;

    @GetMapping("api2/{fcm}/qr")
    private ResponseEntity generateQr(Principal principal, @PathVariable("fcm") String fcm)
        throws IOException {
        return generateQRService.generateQR(principal, fcm);
    }
}
