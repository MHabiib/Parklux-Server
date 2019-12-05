package com.future.pms.repository;

import com.future.pms.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository public interface BookingRepository extends MongoRepository<Booking, String> {
    Page<Booking> findBookingBy(Pageable pageable);

    Booking findBookingByIdBooking(String idBooking);

    Page<Booking> findBookingByIdUser(String idUser, Pageable pageable);

    Booking findBookingByIdUserAndDateOut(String idUser, Long dateOut);

    Integer countAllByDateOutAndIdUser(Long dateOut, String idUser);
}
