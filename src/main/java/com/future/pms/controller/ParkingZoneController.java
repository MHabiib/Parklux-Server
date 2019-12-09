package com.future.pms.controller;

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

    @GetMapping("/detail") public ResponseEntity getParkingZoneDetail(Principal principal) {
        return ResponseEntity.ok(parkingZoneService.getParkingZoneDetail(principal));
    }

    @PostMapping public ResponseEntity createParkingZone(@RequestBody ParkingZone parkingZone) {
        return parkingZoneService.createParkingZone(parkingZone);
    }

    @PostMapping("/add-level")
    public ResponseEntity addParkingLevel(@RequestBody String levelName, Principal principal) {
        return parkingZoneService.addParkingLevel(levelName, principal);
    }

    @PostMapping("/update-level/{id}")
    public ResponseEntity updateLevel(@PathVariable("id") String idLevel,
        @RequestBody String slotsLayout) {
        return parkingZoneService.updateLevel(idLevel, slotsLayout);
    }

    @PostMapping("/update-section")
    public ResponseEntity addParkingSection(@RequestBody String idSection) {
        return parkingZoneService.updateParkingSection(idSection);
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

    @GetMapping("/{idBooking}/parking-layout")
    public ResponseEntity getParkingLayout(@PathVariable("idBooking") String idBooking) {
        return parkingZoneService.getParkingLayout(idBooking);
    }
}
