package com.future.pms.service;

import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ParkingZoneService {
    ResponseEntity<ParkingZone> loadAll();

    ResponseEntity createParkingZone(@RequestBody ParkingZone parkingZone);

    ResponseEntity addParkingLevel(@RequestBody ParkingLevel parkingLevel);

    ResponseEntity addParkingSection(@RequestBody ParkingSection parkingSection);

    ResponseEntity updateParkingSlot(@RequestBody String idParkingSlot, @RequestBody String status);

    ResponseEntity deleteParkingZone(@PathVariable("id") String id);

    ResponseEntity updateParkingZone(String idParkingZone, MultipartFile file
            , String parkingZoneJSON) throws IOException;

    ResponseEntity getImage(String imageName) throws IOException;
}
