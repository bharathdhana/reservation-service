package com.bharath.reservationservice.client;

import com.bharath.reservationservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(
        name = "payment-service",
        path = "/api/payments",
        configuration = FeignConfig.class
)
public interface PaymentClient {

    @PostMapping
    PaymentResponse processPayment(@RequestBody PaymentRequest request);

    record PaymentRequest(Long reservationId, Long userId, BigDecimal amount, String method) {}
    record PaymentResponse(Long id, Long reservationId, Long userId, BigDecimal amount, String method, String status, String transactionId) {}
}
