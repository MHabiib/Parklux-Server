package com.future.pms.repository;

import com.future.pms.model.ParkingSlot;
import com.future.pms.model.list.ParkingZoneLevel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParkingSlotRepository extends MongoRepository <ParkingSlot, String> {

}
