package com.future.pms.service;

import org.springframework.http.ResponseEntity;

public interface GenerateQRService {
    ResponseEntity generateQR(String... args);
}
