package com.bharath.reservationservice.repository;

import com.bharath.reservationservice.entity.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    List<ReservationSeat> findByReservationId(Long reservationId);
    void deleteByReservationId(Long reservationId);

    @Query("""
        SELECT COUNT(rs) > 0
        FROM ReservationSeat rs
        JOIN rs.reservation r
        WHERE rs.showId = :showId
          AND rs.seatId = :seatId
    """)
    boolean isSeatReserved(
            @Param("showId") Long showId,
            @Param("seatId") Long seatId
    );

    boolean existsByShowIdAndSeatId(Long showId, Long seatId);
}