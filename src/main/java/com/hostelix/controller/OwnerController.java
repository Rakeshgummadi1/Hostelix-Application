package com.hostelix.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.LoginResponse;
import com.hostelix.dto.OwnerLoginRequest;
import com.hostelix.dto.OwnerRegistrationRequest;
import com.hostelix.dto.OwnerRegistrationResponse;
import com.hostelix.service.OwnerService;
import com.hostelix.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController {

	private final OwnerService ownerService;
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> ownerLogin(@Valid @RequestBody OwnerLoginRequest loginRequest){
		LoginResponse login = ownerService.login(loginRequest);
		ApiResponse<LoginResponse> response= new ApiResponse<>();
		response.setStatus(HttpStatus.OK.value());
		response.setMessage("Login Successful");
		response.setData(login);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<OwnerRegistrationResponse>> registerOwner(@Valid @RequestBody OwnerRegistrationRequest ownerRegistrationRequest){
		OwnerRegistrationResponse registerOwner = ownerService.registerOwner(ownerRegistrationRequest);
		ApiResponse<OwnerRegistrationResponse> response = ApiResponse.<OwnerRegistrationResponse>builder()
				.status(HttpStatus.CREATED.value())
				.message("Owner Registered Successfully")
				.data(registerOwner)
				.build();
		return new ResponseEntity<>(response,HttpStatus.CREATED);
	}
}
