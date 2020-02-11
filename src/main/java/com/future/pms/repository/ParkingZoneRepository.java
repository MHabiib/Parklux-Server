package com.future.pms.repository;

import com.future.pms.model.parking.ParkingZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository public interface ParkingZoneRepository extends MongoRepository<ParkingZone, String> {
    Page<ParkingZone> findParkingZoneByNameContainingAllIgnoreCase(Pageable pageable, String name);

    ParkingZone findParkingZoneByEmailAdmin(String email);

    ParkingZone findParkingZoneByIdParkingZone(String idParkingZone);

    List<ParkingZone> findParkingZoneByLatitudeNotNullAndLongitudeNotLike(Double latitude);
}
