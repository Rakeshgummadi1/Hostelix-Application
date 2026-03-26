package com.hostelix.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hostelix.dto.OtpResponse;
import com.hostelix.dto.OtpSendRequest;
import com.hostelix.dto.OtpVerifyRequest;

@FeignClient(name = "notification-otp", url = "${notification.service.url}")
public interface OtpClient {
    @PostMapping("/api/notify/otp/send")
    OtpResponse sendOtp(@RequestBody OtpSendRequest request);
    @PostMapping("/api/notify/otp/verify")
    OtpResponse verifyOtp(@RequestBody OtpVerifyRequest request);
}
