package com.hostelix.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.LoginResponse;
import com.hostelix.dto.OtpSendRequest;
import com.hostelix.dto.OtpVerifyRequest;
import com.hostelix.dto.ResidentLoginRequest;
import com.hostelix.service.ResidentService;
import com.hostelix.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resident")
@RequiredArgsConstructor
public class ResidentController {

    private final ResidentService residentService;

    // Existing password-based login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> residentLogin(
            @Valid @RequestBody ResidentLoginRequest loginRequest) {
        LoginResponse login = residentService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Login Successful")
                .data(login)
                .build());
    }

    // --- New OTP Endpoints using DTOs ---

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendOtp(
            @Valid @RequestBody OtpSendRequest request) {
        
        residentService.sendOtp(request.getPhoneNumber());
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("OTP sent successfully to " + request.getPhoneNumber())
                .data(null)
                .build());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request) {
        
        LoginResponse login = residentService.verifyOtpAndLogin(
                request.getPhoneNumber(), request.getOtp());
        
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .status(HttpStatus.OK.value())
                .message("OTP Verified and Login Successful")
                .data(login)
                .build());
    }
    
    @GetMapping("/get-resident/{phoneNumber}")
    public ResponseEntity<ApiResponse<LoginResponse>> getResidentById(@PathVariable String phoneNumber){
    	LoginResponse resident=residentService.getById(phoneNumber);
    	return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
    			.status(HttpStatus.OK.value())
    			.data(resident)
    			.message("Resident Fetched successfully")
    			.build());
    }
}


