package com.bharath.reservationservice.repository;

import com.bharath.reservationservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByShowId(Long showId);
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByShowId(Long showId);
}
