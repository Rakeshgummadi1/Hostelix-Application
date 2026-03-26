package com.hostelix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.ResidentListResponse;
import com.hostelix.dto.ResidentRegistrationRequest;
import com.hostelix.dto.ResidentUpdateRequest;
import com.hostelix.model.Resident;
import com.hostelix.service.ResidentService;
import com.hostelix.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owner/residents")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OwnerResidentController {
	private final ResidentService residentService;

	// 1. PLACE SEARCH FIRST (This prevents the conflict)
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<ResidentListResponse>>> searchResidents(@RequestParam Long hostelId,
			@RequestParam(required = false) Long floorId, @RequestParam(required = false) Long roomId) {
		List<ResidentListResponse> residents = residentService.searchResidents(hostelId, floorId, roomId);
		return ResponseEntity.ok(new ApiResponse<>(200, "Residents fetched", residents));
	}

	// 2. ADD REGEX [0-9]+ TO PROTECT THIS ROUTE
	@GetMapping("/{residentId:[0-9]+}")
	public ResponseEntity<ResidentListResponse> getResidentById(@PathVariable Long residentId) {
		return ResponseEntity.ok(residentService.getResidentById(residentId));
	}

	// A. Add Resident (Onboarding)
	@PostMapping("/register")
	public ResponseEntity<ResidentListResponse> registerResident(
			@Valid @RequestBody ResidentRegistrationRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(residentService.onboardResident(request));
	}

	// B. Get, Search, Filter, and Paginate Residents
	@GetMapping("/hostel/{hostelId}")
	public ResponseEntity<Page<ResidentListResponse>> getResidentsList(@PathVariable Long hostelId,
			@RequestParam(required = false) String search, @RequestParam(required = false) Integer sharing,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		String searchQuery = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
		Integer sharingFilter = (sharing != null && sharing > 0) ? sharing : null;
		Page<ResidentListResponse> paginatedResponses = residentService.getResidents(hostelId, searchQuery,
				sharingFilter, page, size);
		return ResponseEntity.ok(paginatedResponses);
	}

	// D. Update Resident Details
	@PutMapping("/{residentId:[0-9]+}")
	public ResponseEntity<Resident> updateResident(@PathVariable Long residentId,
			@Valid @RequestBody ResidentUpdateRequest request) {
		return ResponseEntity.ok(residentService.updateResident(residentId, request));
	}

	// E. Vacate Resident
	@DeleteMapping("/{residentId:[0-9]+}/vacate")
	public ResponseEntity<ApiResponse<Void>> vacateResident(@PathVariable Long residentId) {
		residentService.vacateResident(residentId);
		return ResponseEntity.ok(ApiResponse.<Void>builder().status(HttpStatus.OK.value())
				.message("Resident vacated successfully and bed is now available").build());
	}
}
