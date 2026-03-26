package com.hostelix.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hostelix.dto.EmailRequestDTO;

@FeignClient(name = "notification-service", url="${notification.service.url}")
public interface NotificationClient {

	@PostMapping("/api/notify/welcome")
    void sendWelcomeEmail(@RequestBody EmailRequestDTO request);
}
