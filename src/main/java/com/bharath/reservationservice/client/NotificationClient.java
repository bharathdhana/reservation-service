package com.bharath.reservationservice.client;

import com.bharath.reservationservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "notification-service",
        path = "/api/notifications",
        configuration = FeignConfig.class
)
public interface NotificationClient {

    @PostMapping
    NotificationResponse sendNotification(@RequestBody NotificationRequest request);

    record NotificationRequest(Long userId, String recipientEmail, String type, String subject, String message) {}
    record NotificationResponse(Long id, Long userId, String recipientEmail, String type, String subject, String message, String status) {}
}
