package com.future.pms.repository;

import com.future.pms.model.parking.ParkingSection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository public interface ParkingSectionRepository
    extends MongoRepository<ParkingSection, String> {
    ParkingSection findParkingSectionByIdSection(String idSection);

    ParkingSection findParkingSectionBySectionNameAndIdLevel(String sectionName, String idLevel);

    List<ParkingSection> findParkingSectionByIdLevel(String idLevel);
}
