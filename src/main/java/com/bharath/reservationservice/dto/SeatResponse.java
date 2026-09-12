package com.bharath.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatResponse {
    private Long id;
    private Long screenId;
    private String seatNumber;
    private String rowNumber;
    private String seatType;
}
