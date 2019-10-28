package com.future.pms.repository;

import com.future.pms.model.parking.ParkingLevel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLevelRepository extends MongoRepository<ParkingLevel, String> {
    ParkingLevel findByIdLevel(String idLevel);
}
