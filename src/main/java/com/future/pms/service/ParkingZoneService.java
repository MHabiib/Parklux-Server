package com.future.pms.service;

import com.future.pms.model.parking.ParkingZone;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

public interface ParkingZoneService {
    ResponseEntity loadAll(Integer page);

    ResponseEntity createParkingZone(@RequestBody ParkingZone parkingZone);

    ResponseEntity addParkingLevel(@RequestBody String levelName, Principal principal);

    ResponseEntity updateParkingSlot(@RequestBody String idParkingSlot, @RequestBody String status);

    ResponseEntity updateParkingSection(@RequestBody String idSection);

    ResponseEntity updateLevel(String idLevel, String slotsLayout);

    ResponseEntity updateParkingZone(Principal principal, MultipartFile file,
        String parkingZoneJSON) throws IOException;

    ResponseEntity getParkingLayout(String idBooking);

    ResponseEntity getImage(String imageName) throws IOException;
}
