package com.future.pms.service.impl;

import com.future.pms.model.ParkingZone;
import com.future.pms.model.list.ParkingZoneLevel;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingZoneServiceImpl implements ParkingZoneService {

    @Autowired
    ParkingZoneRepository parkingZoneRepository;

    @Override
    public ResponseEntity loadAll() {
        return ResponseEntity.ok(parkingZoneRepository.findAll());
    }

    @Override
    public ResponseEntity createParkingZone(ParkingZone parkingZone)
    {
        return ResponseEntity.ok(parkingZoneRepository.save(parkingZone));
    }

    @Override
    public ResponseEntity addParkingLevel(ParkingZoneLevel parkingZoneLevel){
        ParkingZone parkingZone = parkingZoneRepository
                .findParkingZoneByName(parkingZoneLevel.getParkingZoneName());
        List<ParkingZoneLevel> parkingZoneLevels = new ArrayList<>();
        ParkingZoneLevel level = new ParkingZoneLevel();

        level.setLevelName(parkingZoneLevel.getLevelName());
        parkingZoneLevels.add(level);
       // parkingZone.setParkingZoneLevels(parkingZoneLevels);
        parkingZoneRepository.save(parkingZone);
        return new ResponseEntity(HttpStatus.OK);
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
