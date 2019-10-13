package com.future.pms.controller;

import com.future.pms.model.ParkingZone;
import com.future.pms.model.list.ParkingZoneLevel;
import com.future.pms.service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("**")
@RestController
@RequestMapping("/api/parking-zone")
public class ParkingZoneController {
    
    @Autowired
    ParkingZoneService parkingZoneService;

    @GetMapping
    public ResponseEntity loadAll() {
        return ResponseEntity.ok(parkingZoneService.loadAll());
    }

    @PostMapping
    public ResponseEntity createParkingZone(@RequestBody ParkingZone parkingZone) {
        return parkingZoneService.createParkingZone(parkingZone);
    }

    @PostMapping("/add-level")
    public ResponseEntity addParkingLevel(ParkingZoneLevel parkingZoneLevel){
        return parkingZoneService.addParkingLevel(parkingZoneLevel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingZone> updateParkingZone(@PathVariable("id") String id,
                                                         @RequestBody ParkingZone parkingZone) {
        return parkingZoneService.updateParkingZone(id,parkingZone);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteParkingZone(@PathVariable("id") String id) {
        return parkingZoneService.deleteParkingZone(id);
    }
}
