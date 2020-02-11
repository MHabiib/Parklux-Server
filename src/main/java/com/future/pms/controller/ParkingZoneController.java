package com.future.pms.controller;

import com.future.pms.model.request.LevelDetailsRequest;
import com.future.pms.service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@CrossOrigin("**") @RestController @RequestMapping public class ParkingZoneController {
    @Autowired ParkingZoneService parkingZoneService;

    @GetMapping("/api3/parking-zone") public ResponseEntity loadAll(Integer page, String name) {
        return parkingZoneService.loadAll(page, name);
    }

    @GetMapping("/api2/parking-zone/detail")
    public ResponseEntity getParkingZoneDetail(Principal principal) {
        return parkingZoneService.getParkingZoneDetail(principal);
    }

    @GetMapping("/api3/{id}/parking-zone")
    public ResponseEntity getAdminSA(@PathVariable("id") String id) {
        return ResponseEntity.ok(parkingZoneService.getAdminSA(id));
    }

    @PostMapping("/api2/parking-zone/add-level")
    public ResponseEntity addParkingLevel(@RequestBody String levelName, Principal principal) {
        return parkingZoneService.addParkingLevel(levelName, principal);
    }

    @PostMapping("/api2/parking-zone/update-level/{id}")
    public ResponseEntity updateLevel(@PathVariable("id") String idLevel,
        @RequestBody String slotsLayout) {
        return parkingZoneService.updateLevel(idLevel, slotsLayout);
    }

    @PostMapping("/api2/parking-zone/update-section")
    public ResponseEntity updateParkingSection(@RequestBody String idSection) {
        return parkingZoneService.updateParkingSection(idSection);
    }

    @PutMapping("/api2/parking-zone/update-zone")
    public ResponseEntity updateParkingZone(Principal principal,
        @RequestPart("parkingZone") String parkingZoneJSON) throws IOException {
        return parkingZoneService.updateParkingZone(principal, parkingZoneJSON);
    }

    @PutMapping("/api3/parking-zone/{id}/update-zone")
    public ResponseEntity updateAdmin(@PathVariable("id") String id,
        @RequestPart("parkingZone") String parkingZoneJSON) throws IOException {
        return parkingZoneService.updateAdmin(id, parkingZoneJSON);
    }

    @PutMapping("/api2/parking-zone/update-zone/picture")
    public ResponseEntity updateParkingZonePicture(Principal principal,
        @RequestPart("file") MultipartFile file) {
        return parkingZoneService.updateParkingZonePicture(principal, file);
    }

    @PutMapping("/api2/parking-zone/update-level")
    public ResponseEntity updateParkingLevel(@RequestBody LevelDetailsRequest levelDetailsRequest,
        Principal principal) {
        return parkingZoneService.updateParkingLevel(levelDetailsRequest, principal);
    }

    @GetMapping("/api2/parking-zone/levels") public ResponseEntity getLevels(Principal principal) {
        return parkingZoneService.getLevels(principal);
    }

    @GetMapping("/api/parking-zone/{idBooking}/parking-layout")
    public ResponseEntity getParkingBookingLayout(@PathVariable("idBooking") String idBooking) {
        return parkingZoneService.getParkingBookingLayout(idBooking);
    }

    @GetMapping("/api2/parking-zone/{idLevel}/level-layout")
    public ResponseEntity getParkingLevelBookingLayout(@PathVariable("idLevel") String idLevel) {
        return parkingZoneService.getParkingLevelLayout(idLevel);
    }

    @GetMapping("/api2/parking-zone/{idLevel}/section-details")
    public ResponseEntity getSectionDetails(@PathVariable("idLevel") String idLevel) {
        return parkingZoneService.getSectionDetails(idLevel);
    }

    @PostMapping("/api2/parking-zone/level/edit-mode/{id}/{mode}")
    public ResponseEntity editModeParkingLevel(@PathVariable("id") String idLevel,
        @PathVariable("mode") String mode) {
        return parkingZoneService.editModeParkingLevel(idLevel, mode);
    }

    @GetMapping("/api/parking-zone/latlng") public ResponseEntity getLatLng() {
        return parkingZoneService.getLatLng();
    }
}
