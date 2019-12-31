package com.future.pms.controller;

import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.request.LevelDetailsRequest;
import com.future.pms.service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity updateParkingSection(@RequestBody String idSection) {
        return parkingZoneService.updateParkingSection(idSection);
    }

    @PostMapping("update-slot/{id}")
    public ResponseEntity updateParkingSlot(@PathVariable("id") String idParkingSlot,
        @RequestBody String status) {
        return parkingZoneService.updateParkingSlot(idParkingSlot, status);
    }

    @PutMapping(value = "update-zone", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateParkingZone(Principal principal,
        @RequestPart("parkingZone") String parkingZoneJSON) throws IOException {
        return parkingZoneService.updateParkingZone(principal, parkingZoneJSON);
    }

    @PutMapping(value = "update-zone/picture", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateParkingZonePicture(Principal principal,
        @RequestPart("file") MultipartFile file) {
        return parkingZoneService.updateParkingZonePicture(principal, file);
    }

    @PutMapping("update-level")
    public ResponseEntity updateParkingLevel(@RequestBody LevelDetailsRequest levelDetailsRequest,
        Principal principal) {
        return parkingZoneService.updateParkingLevel(levelDetailsRequest, principal);
    }

    @GetMapping("/levels") public ResponseEntity getLevels(Principal principal) {
        return parkingZoneService.getLevels(principal);
    }

    @GetMapping("/{idBooking}/parking-layout")
    public ResponseEntity getParkingBookingLayout(@PathVariable("idBooking") String idBooking) {
        return parkingZoneService.getParkingBookingLayout(idBooking);
    }

    @GetMapping("/{idLevel}/level-layout")
    public ResponseEntity getParkingLevelBookingLayout(@PathVariable("idLevel") String idLevel) {
        return parkingZoneService.getParkingLevelLayout(idLevel);
    }

    @GetMapping("/{idLevel}/section-details")
    public ResponseEntity getSectionDetails(@PathVariable("idLevel") String idLevel) {
        return parkingZoneService.getSectionDetails(idLevel);
    }

    @PostMapping("/level/edit-mode/{id}/{mode}")
    public ResponseEntity editModeParkingLevel(@PathVariable("id") String idLevel,
        @PathVariable("mode") String mode) {
        return parkingZoneService.editModeParkingLevel(idLevel, mode);
    }
}
