package com.future.pms.service;

import com.future.pms.model.ParkingZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ParkingZoneService {
    ResponseEntity<ParkingZone> loadAll();
    ResponseEntity createParkingZone(@RequestBody ParkingZone parkingZone);
    ResponseEntity <ParkingZone> updateParkingZone(@PathVariable("id") String id, @RequestBody ParkingZone parkingZone);
    ResponseEntity deleteParkingZone(@PathVariable("id") String id);
}
