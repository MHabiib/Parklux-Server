package com.future.pms.repository;

import com.future.pms.model.parking.ParkingLevel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository public interface ParkingLevelRepository extends MongoRepository<ParkingLevel, String> {
    ParkingLevel findByIdLevel(String idLevel);

    List<ParkingLevel> findByIdParkingZoneOrderByLevelName(String idParkingZone);
}
