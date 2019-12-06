package com.future.pms.service;

import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingZone;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

public interface ParkingZoneService {
    ResponseEntity loadAll(Integer page);

    ResponseEntity createParkingZone(@RequestBody ParkingZone parkingZone);

    ResponseEntity addParkingLevel(@RequestBody ParkingLevel parkingLevel);

    ResponseEntity updateParkingSlot(@RequestBody String idParkingSlot, @RequestBody String status);

    ResponseEntity deleteParkingZone(@PathVariable("id") String id);

    ResponseEntity updateParkingZone(Principal principal, MultipartFile file,
        String parkingZoneJSON) throws IOException;

    ResponseEntity getImage(String imageName) throws IOException;
}
