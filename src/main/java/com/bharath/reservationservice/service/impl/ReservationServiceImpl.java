package com.bharath.reservationservice.service.impl;

import com.bharath.reservationservice.client.SeatClient;
import com.bharath.reservationservice.client.ShowClient;
import com.bharath.reservationservice.client.UserClient;
import com.bharath.reservationservice.dto.*;
import com.bharath.reservationservice.entity.Reservation;
import com.bharath.reservationservice.entity.ReservationSeat;
import com.bharath.reservationservice.entity.enums.BookingStatus;
import com.bharath.reservationservice.exception.ResourceNotFoundException;
import com.bharath.reservationservice.repository.ReservationRepository;
import com.bharath.reservationservice.repository.ReservationSeatRepository;
import com.bharath.reservationservice.service.ReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ShowClient showClient;
    private final UserClient userClient;
    private final SeatClient seatClient;

    @Override
    @Transactional
    public ReservationResponse createReservation(Long userId, ReservationRequest request) {

        ShowResponse show = showClient.getShowById(request.getShowId());
        if (show == null) {
            throw new ResourceNotFoundException("Show not found");
        }

        validateSeatSelection(request);
        List<SeatResponse> seats = new ArrayList<>();

        for(Long seatId : request.getSeatIds()) {
            SeatResponse seat = seatClient.getSeatById(seatId);

            if (seat == null)
                throw  new ResourceNotFoundException(" Seat not found" +  seatId);

            if (!seat.getScreenId().equals(show.getScreenId()))
                throw new IllegalArgumentException(" Seat does not belong to this show's screen: ");

            if (reservationSeatRepository.isSeatReserved(show.getId(), seatId))
                throw new IllegalArgumentException(" Seat already booked " + seatId);

            seats.add(seat);
        }
        BigDecimal totalAmount = show.getTicketPrice().multiply(BigDecimal.valueOf(seats.size()));

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .showId(show.getId())
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .bookedAt(LocalDateTime.now())
                .build();

        reservationRepository.save(reservation);

        for(SeatResponse seat : seats) {
            ReservationSeat reservationSeat = ReservationSeat.builder()
                    .reservation(reservation)
                    .seatId(seat.getId())
                    .showId(show.getId())
                    .build();

            reservationSeatRepository.save(reservationSeat);
        }
        return mapToReservationResponses(reservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream().map(this::mapToReservationResponses).toList();
    }

    @Override
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        return mapToReservationResponses(reservation);
    }

    @Override
    public List<ReservationResponse> getReservationByUser(Long userId) {
        UserResponse user = userClient.getUserById(userId);
        if(user == null)
            throw new ResourceNotFoundException("User not found");
        return reservationRepository.findByUserId(userId)
                .stream().map(this::mapToReservationResponses).toList();
    }

    @Override
    public List<ReservationResponse> getReservationByShow(Long showId) {
        ShowResponse show = showClient.getShowById(showId);
        if(show == null)
            throw new ResourceNotFoundException("Show not found");
        return reservationRepository.findByShowId(showId)
                .stream().map(this::mapToReservationResponses).toList();
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if(reservation.getStatus() == BookingStatus.CANCELLED)
            throw new ResourceNotFoundException("Reservation is already cancelled");

        reservation.setStatus(BookingStatus.CANCELLED);
        reservationRepository.save(reservation);
        return mapToReservationResponses(reservation);
    }

    @Override
    @Transactional
    public String deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        reservationSeatRepository.deleteByReservationId(id);
        reservationRepository.delete(reservation);
        return "Reservation seat deleted successfully";
    }

    private ReservationResponse mapToReservationResponses(Reservation reservation) {

        List<Long> seatIds = reservationSeatRepository.findByReservationId(reservation.getId())
                .stream().map(ReservationSeat::getSeatId).toList();

        return ReservationResponse.builder()
                .id(reservation.getId())
                .bookingNumber(reservation.getBookingNumber())
                .userId(reservation.getUserId())
                .showId(reservation.getShowId())
                .totalAmount(reservation.getTotalAmount())
                .status(reservation.getStatus())
                .bookedAt(reservation.getBookedAt())
                .seatIds(seatIds)
                .build();
    }

    private void validateSeatSelection(ReservationRequest request) {
        if(request.getSeatIds() == null || request.getSeatIds().isEmpty())
            throw new IllegalArgumentException("At least one seat must be selected");

        if (request.getSeatIds().size() != request.getSeatIds().stream().distinct().count())
            throw new IllegalArgumentException("Duplicate seats are not allowed");
    }
}
