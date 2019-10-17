/*
package com.future.pms.controller;

import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
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
    public ResponseEntity addParkingLevel(@RequestBody ParkingLevel parkingLevel){
        return parkingZoneService.addParkingLevel(parkingLevel);
    }

    @PostMapping("/add-section")
    public ResponseEntity addParkingSection(@RequestBody ParkingSection parkingSection){
        return parkingZoneService.addParkingSection(parkingSection);
    }

    @PostMapping("/add-slot")
    public ResponseEntity addParkingSlot(@RequestBody ParkingSlot parkingSlot){
        return parkingZoneService.addParkingSlot(parkingSlot);
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
*/
