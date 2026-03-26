package com.hostelix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.ResidentDashboardDTO;
import com.hostelix.dto.ResidentProfileDTO;
import com.hostelix.service.ResidentDashboardService;
import com.hostelix.service.ResidentProfileService;
import com.hostelix.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resident")
@RequiredArgsConstructor
public class ResidentDashboardController {
	private final ResidentDashboardService dashboardService;
	private final ResidentProfileService profileService;

	@GetMapping("/dashboard/{residentId}")
	public ResponseEntity<ApiResponse<ResidentDashboardDTO>> getDashboard(@PathVariable Long residentId) {
		return ResponseEntity.ok(new ApiResponse<>(200, "Dashboard data fetched successfully",
				dashboardService.getDashboardSummary(residentId)));
	}
	
	@GetMapping("/profile/{residentId}")
	public ResponseEntity<ApiResponse<ResidentProfileDTO>> getProfile(
	        @PathVariable Long residentId) {
	    ResidentProfileDTO profile = profileService.getResidentProfile(residentId);
	    return ResponseEntity.ok(
	        new ApiResponse<>(200, "Profile fetched successfully", profile));
	}
}
