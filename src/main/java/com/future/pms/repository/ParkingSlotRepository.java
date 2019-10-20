package com.future.pms.repository;


import com.future.pms.model.parking.ParkingSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSlotRepository extends MongoRepository<ParkingSlot, String> {
    ParkingSlot findFirstByIdParkingZoneAndStatus(String idParkingZone, String status);
}
