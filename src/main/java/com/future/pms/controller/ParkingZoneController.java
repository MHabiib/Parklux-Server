package com.future.pms.controller;

import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@CrossOrigin("**") @RestController @RequestMapping("/api/parking-zone")
public class ParkingZoneController {
    @Autowired ParkingZoneService parkingZoneService;

    @GetMapping public ResponseEntity loadAll(Integer page) {
        return ResponseEntity.ok(parkingZoneService.loadAll(page));
    }

    @PostMapping public ResponseEntity createParkingZone(@RequestBody ParkingZone parkingZone) {
        return parkingZoneService.createParkingZone(parkingZone);
    }

    @PostMapping("/add-level")
    public ResponseEntity addParkingLevel(@RequestBody ParkingLevel parkingLevel) {
        return parkingZoneService.addParkingLevel(parkingLevel);
    }

    @PostMapping("/add-section")
    public ResponseEntity addParkingSection(@RequestBody ParkingSection parkingSection) {
        return parkingZoneService.addParkingSection(parkingSection);
    }

    @PostMapping("update-slot/{id}")
    public ResponseEntity updateParkingSlot(@PathVariable("id") String idParkingSlot,
        @RequestBody String status) {
        return parkingZoneService.updateParkingSlot(idParkingSlot, status);
    }

    @PutMapping(value = "update-zone", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateParkingZone(Principal principal,
        @Nullable @RequestPart("file") MultipartFile file,
        @RequestPart("parkingZone") String parkingZoneJSON) throws IOException {
        return parkingZoneService.updateParkingZone(principal, file, parkingZoneJSON);
    }
}
