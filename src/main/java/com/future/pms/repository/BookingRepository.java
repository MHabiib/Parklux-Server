package com.future.pms.repository;

import com.future.pms.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findAll();

    Booking findBookingByIdBooking(String idBooking);

    List<Booking> findBookingByIdUser(String idUser);
}
