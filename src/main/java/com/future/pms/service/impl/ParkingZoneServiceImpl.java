package com.future.pms.service.impl;

import com.future.pms.model.ParkingZone;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    public ResponseEntity<ParkingZone> updateParkingZone(String id, ParkingZone parkingZone) {
        return null;
    }

    @Override
    public ResponseEntity deleteParkingZone(String id) {
        return null;
    }
}
