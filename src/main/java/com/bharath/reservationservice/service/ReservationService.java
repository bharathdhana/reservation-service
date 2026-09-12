package com.bharath.reservationservice.service;

import com.bharath.reservationservice.dto.ReservationRequest;
import com.bharath.reservationservice.dto.ReservationResponse;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(Long userId, ReservationRequest reservationRequest);
    List<ReservationResponse> getAllReservations();
    ReservationResponse getReservationById(Long id);
    List<ReservationResponse> getReservationByUser(Long userId);
    List<ReservationResponse> getReservationByShow(Long showId);
    ReservationResponse cancelReservation(Long id);
    String deleteReservation(Long id);
}
