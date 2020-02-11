package com.future.pms.service;

import com.future.pms.model.request.LevelDetailsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

public interface ParkingZoneService {
    ResponseEntity loadAll(Integer page, String name);

    ResponseEntity getParkingZoneDetail(Principal principal);

    ResponseEntity addParkingLevel(String levelName, Principal principal);

    ResponseEntity updateParkingSection(String idSection);

    ResponseEntity updateLevel(String idLevel, String slotsLayout);

    ResponseEntity updateParkingZone(Principal principal, String parkingZoneJSON)
        throws IOException;

    ResponseEntity updateAdmin(String id, String parkingZoneJSON) throws IOException;

    ResponseEntity updateParkingLevel(LevelDetailsRequest levelDetailsRequest, Principal principal);

    ResponseEntity getLevels(Principal principal);

    ResponseEntity getParkingBookingLayout(String idBooking);

    ResponseEntity getParkingLevelLayout(String idLevel);

    ResponseEntity getSectionDetails(String idLevel);

    ResponseEntity editModeParkingLevel(String idLevel, String mode);

    ResponseEntity updateParkingZonePicture(Principal principal, MultipartFile file);

    ResponseEntity getAdminSA(String id);

    ResponseEntity getLatLng();
}
