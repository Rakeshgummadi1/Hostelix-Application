package com.hostelix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.AnnouncementRequest;
import com.hostelix.dto.AnnouncementResponse;
import com.hostelix.service.AnnouncementService;
import com.hostelix.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
	private final AnnouncementService announcementService;

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<AnnouncementResponse>> create(@RequestBody AnnouncementRequest request) {
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Posted successfully", announcementService.createAnnouncement(request)));
	}

	@GetMapping("/hostel/{hostelId}")
	public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getByHostel(@PathVariable Long hostelId) {
		return ResponseEntity.ok(
				new ApiResponse<>(200, "Announcements fetched", announcementService.getHostelAnnouncements(hostelId)));
	}

	@GetMapping("/resident/{residentId}")
	public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getByResident(@PathVariable Long residentId) {
		return ResponseEntity.ok(new ApiResponse<>(200, "Relevant updates fetched",
				announcementService.getResidentAnnouncements(residentId)));
	}
}
