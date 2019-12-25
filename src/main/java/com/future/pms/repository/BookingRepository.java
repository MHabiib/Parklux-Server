package com.future.pms.repository;

import com.future.pms.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository public interface BookingRepository extends MongoRepository<Booking, String> {
    Page<Booking> findBookingBy(Pageable pageable);

    Booking findBookingByIdBooking(String idBooking);

    List<Booking> findBookingByIdUser(String idUser);

    //    Page<Booking> findBookingByIdUser(String idUser, Pageable pageable);

    Page<Booking> findBookingByIdParkingZoneAndDateOut(String idParkingZone, Long dateOut,
        Pageable pageable);

    Page<Booking> findBookingByIdParkingZoneAndDateOutNotNull(String idParkingZone,
        Pageable pageable);

    Booking findBookingByIdUserAndDateOut(String idUser, Long dateOut);

    Integer countAllByDateOutAndIdUser(Long dateOut, String idUser);
}
