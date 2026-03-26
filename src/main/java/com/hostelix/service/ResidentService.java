package com.hostelix.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.hostelix.dto.LoginResponse;
import com.hostelix.dto.ResidentListResponse;
import com.hostelix.dto.ResidentLoginRequest;
import com.hostelix.dto.ResidentRegistrationRequest;
import com.hostelix.dto.ResidentUpdateRequest;
import com.hostelix.model.Resident;

public interface ResidentService {

	LoginResponse login(ResidentLoginRequest loginRequest);

	void sendOtp(String phoneNumber);

	LoginResponse verifyOtpAndLogin(String phoneNumber, String otp);

	ResidentListResponse onboardResident(ResidentRegistrationRequest request);

	Page<ResidentListResponse> getResidents(Long hostelId, String search, Integer sharing, int page, int size);

	ResidentListResponse getResidentById(Long residentId);

	Resident updateResident(Long residentId, ResidentUpdateRequest request);

	void vacateResident(Long residentId);
	
	public LoginResponse getById(String phoneNumber);
	
	List<ResidentListResponse> searchResidents(Long hostelId, Long floorId, Long roomId);
}
