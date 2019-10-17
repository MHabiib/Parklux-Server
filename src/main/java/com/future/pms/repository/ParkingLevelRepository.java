package com.future.pms.repository;

import com.future.pms.model.parking.ParkingLevel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface ParkingLevelRepository extends MongoRepository<ParkingLevel, String> {
    ParkingLevel findByIdParkingZoneAndLevelName(String idParkingZone, String levelName);
}
