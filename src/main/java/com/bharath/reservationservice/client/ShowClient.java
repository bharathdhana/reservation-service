package com.bharath.reservationservice.client;

import com.bharath.reservationservice.config.FeignConfig;
import com.bharath.reservationservice.dto.ShowResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "show-service",
        path = "/api/show",
        configuration = FeignConfig.class
)
public interface ShowClient {
    @GetMapping("/{id}")
    ShowResponse getShowById(@PathVariable("id") Long id);
}
