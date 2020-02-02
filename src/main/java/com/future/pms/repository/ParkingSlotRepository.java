package com.future.pms.repository;


import com.future.pms.model.parking.ParkingSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository public interface ParkingSlotRepository extends MongoRepository<ParkingSlot, String> {
    List<ParkingSlot> findAllByIdParkingZoneAndStatus(String idParkingZone, String status);

    List<ParkingSlot> findAllByIdLevel(String idLevel);

    ParkingSlot findByIdSlot(String idSlot);

    ParkingSlot findByIdParkingZoneAndSlotNumberInLayout(String idParkingZone, int slotNumber);
}
