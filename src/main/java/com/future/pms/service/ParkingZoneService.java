package com.future.pms.service;

import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface ParkingZoneService {
    ResponseEntity<ParkingZone> loadAll();
    ResponseEntity createParkingZone(@RequestBody ParkingZone parkingZone);
    ResponseEntity addParkingLevel(@RequestBody ParkingLevel parkingLevel);
    ResponseEntity addParkingSection(@RequestBody ParkingSection parkingSection);
    ResponseEntity <ParkingZone> updateParkingZone(@PathVariable("id") String id, @RequestBody ParkingZone parkingZone);
    ResponseEntity deleteParkingZone(@PathVariable("id") String id);
}
