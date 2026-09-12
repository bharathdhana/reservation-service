package com.bharath.reservationservice.client;

import com.bharath.reservationservice.config.FeignConfig;
import com.bharath.reservationservice.dto.SeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "theatre-service",
        path = "/api/seat",
        configuration = FeignConfig.class
)
public interface SeatClient {
    @GetMapping("/{id}")
    SeatResponse getSeatById(@PathVariable("id") Long id);
}