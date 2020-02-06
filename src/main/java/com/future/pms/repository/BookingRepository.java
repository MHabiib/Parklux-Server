package com.future.pms.repository;

import com.future.pms.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository public interface BookingRepository extends MongoRepository<Booking, String> {
    Page<Booking> findBookingBy(Pageable pageable);

    Page<Booking> findBookingByDateOutNotNull(Pageable pageable);

    Page<Booking> findBookingByDateOutNull(Pageable pageable);

    Booking findBookingByIdBooking(String idBooking);

    Page<Booking> findBookingByIdUserAndTotalPriceNotNull(String idUser, Pageable pageable);

    Page<Booking> findBookingByIdParkingZoneAndDateOut(String idParkingZone, Long dateOut,
        Pageable pageable);

    Page<Booking> findBookingByIdParkingZoneAndDateOutNotNull(String idParkingZone,
        Pageable pageable);

    Booking findBookingByIdUserAndDateOut(String idUser, Long dateOut);

    Booking findBookingByIdUserAndTotalPrice(String idUser, String totalPrice);

    Booking findBookingByIdSlotAndDateOutNull(String idSlot);

    Integer countAllByDateOutAndIdUser(Long dateOut, String idUser);
}
