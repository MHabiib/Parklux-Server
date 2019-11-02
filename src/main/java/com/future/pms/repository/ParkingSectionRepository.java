package com.future.pms.repository;

import com.future.pms.model.parking.ParkingSection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSectionRepository extends MongoRepository<ParkingSection, String> {
    ParkingSection findParkingSectionByIdSection(String idSection);
}
