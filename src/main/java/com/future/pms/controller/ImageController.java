package com.future.pms.controller;


import com.future.pms.service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin @RestController @RequestMapping("/img") public class ImageController {
    @Autowired ParkingZoneService parkingZoneService;

    @GetMapping(value = "/{imageName:.+}")
    public ResponseEntity getOldImage(@PathVariable("imageName") String imageName)
        throws IOException {
        return parkingZoneService.getImage(imageName);
    }

    @GetMapping(value = "/{type}/{imageName:.+}")
    public ResponseEntity getPaymentImage(@PathVariable("type") String type,
        @PathVariable("imageName") String imageName) throws IOException {
        return parkingZoneService.getImage(type + "/" + imageName);
    }
}
