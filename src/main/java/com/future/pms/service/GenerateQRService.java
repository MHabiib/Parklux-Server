package com.future.pms.service;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.Principal;

public interface GenerateQRService {
    ResponseEntity generateQR(Principal principal);

    ResponseEntity getImage(String imageName) throws IOException;
}
