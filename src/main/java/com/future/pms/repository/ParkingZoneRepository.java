package com.future.pms.repository;

import com.future.pms.model.parking.ParkingZone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingZoneRepository extends MongoRepository<ParkingZone, String> {
    List<ParkingZone> findAll();

    ParkingZone findParkingZoneByIdParkingZone(String idParkingZone);
}
