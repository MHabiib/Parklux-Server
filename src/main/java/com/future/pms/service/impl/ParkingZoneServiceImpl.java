package com.future.pms.service.impl;

import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
import com.future.pms.model.request.CreateParkingSlotRequest;
import com.future.pms.repository.ParkingLevelRepository;
import com.future.pms.repository.ParkingSectionRepository;
import com.future.pms.repository.ParkingSlotRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.ParkingZoneService;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ParkingZoneServiceImpl implements ParkingZoneService {

    @Autowired
    ParkingZoneRepository parkingZoneRepository;

    @Autowired
    ParkingLevelRepository parkingLevelRepository;

    @Autowired
    ParkingSectionRepository parkingSectionRepository;

    @Autowired
    ParkingSlotRepository parkingSlotRepository;

    @Override
    public ResponseEntity loadAll() {
        return ResponseEntity.ok(parkingZoneRepository.findAll());
    }

    @Override
    public ResponseEntity createParkingZone(@RequestBody ParkingZone parkingZone) {
        return ResponseEntity.ok(parkingZoneRepository.save(parkingZone));
    }

    @Override
    public ResponseEntity addParkingLevel(@RequestBody ParkingLevel parkingLevel){
        ParkingZone parkingZoneExist = parkingZoneRepository
                .findParkingZoneByIdParkingZone(parkingLevel.getIdParkingZone());
        if (parkingZoneExist != null) {
            return ResponseEntity.ok(parkingLevelRepository.save(parkingLevel));
        } else {
            return new ResponseEntity<>("Parking Zone Not Found", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity addParkingSection(@RequestBody ParkingSection parkingSection) {
        ParkingLevel parkingLevelExist = parkingLevelRepository.findByIdLevel(parkingSection.getIdLevel());
        if (parkingLevelExist != null) {
            parkingSectionRepository.save(parkingSection);
            CreateParkingSlotRequest parkingSlotRequest = new CreateParkingSlotRequest();
            parkingSlotRequest.setIdParkingZone(parkingLevelExist.getIdParkingZone());
            parkingSlotRequest.setIdSection(parkingSection.getIdSection());

            for (int i = 1 ; i<=20 ; i++){
                parkingSlotRequest.setSlotName(parkingSection.getSectionName() + " - " + i);
                addSlot(parkingSlotRequest);
            }
            return ResponseEntity.ok("Parking Section Created and Adds 20 Slot on " + parkingSection.getSectionName());
        } else {
            return new ResponseEntity<>("Parking Level Not Found", HttpStatus.BAD_REQUEST);
        }
    }

    private void addSlot (CreateParkingSlotRequest slotRequest) {
        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setIdSection(slotRequest.getIdSection());
        parkingSlot.setIdParkingZone(slotRequest.getIdParkingZone());
        parkingSlot.setName(slotRequest.getSlotName());
        parkingSlotRepository.save(parkingSlot);
    }




    @Override
    public ResponseEntity<ParkingZone> updateParkingZone(String id, ParkingZone parkingZone) {
        return null;
    }

    @Override
    public ResponseEntity deleteParkingZone(String id) {
        return null;
    }
}
