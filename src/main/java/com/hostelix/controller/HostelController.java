package com.hostelix.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.HostelResponseDTO;
import com.hostelix.dto.OccupancyStatsDTO;
import com.hostelix.dto.RoomFilterRequest;
import com.hostelix.dto.pgconfig.HostelConfigRequest;
import com.hostelix.service.HostelService;
import com.hostelix.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hostel")
@RequiredArgsConstructor
public class HostelController {

	private final HostelService hostelService;

	@PostMapping("/config")
	public ResponseEntity<ApiResponse<HostelResponseDTO>> saveHostelConfig(
			@Valid @RequestBody HostelConfigRequest hostelConfigRequest, @RequestParam Long ownerId) {
		ApiResponse<HostelResponseDTO> response = new ApiResponse<HostelResponseDTO>();
		response.setStatus(HttpStatus.CREATED.value());
		response.setMessage("Property and beds generated successfully");
		response.setData(hostelService.SaveConfiguration(ownerId, hostelConfigRequest));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/status")
	public ResponseEntity<ApiResponse<HostelResponseDTO>> getStatus(
	        @RequestParam Long ownerId,
	        @ModelAttribute RoomFilterRequest filters) { 
	    return ResponseEntity.ok(ApiResponse.<HostelResponseDTO>builder()
	            .status(HttpStatus.OK.value())
	            .message("Property status fetched successfully")
	            .data(hostelService.getHostelStatus(ownerId, filters))
	            .build());
	}

	@GetMapping("/stats")
	public ResponseEntity<ApiResponse<OccupancyStatsDTO>> getStats(@RequestParam Long ownerId) {
		return ResponseEntity.ok(ApiResponse.<OccupancyStatsDTO>builder().status(HttpStatus.OK.value())
				.message("Occupancy stats fetched successfully").data(hostelService.getOccupancyStats(ownerId))
				.build());
	}
	
	@GetMapping("/config")
	public ResponseEntity<ApiResponse<HostelResponseDTO>> getConfiguration(@RequestParam Long ownerId){
		return ResponseEntity.ok(ApiResponse.<HostelResponseDTO>builder().status(HttpStatus.OK.value())
				.message("Configuration fetched successfully")
				.data(hostelService.getHostelConfig(ownerId))
				.build());
	}
}
