package com.hostelix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.ComplaintRequest;
import com.hostelix.dto.ComplaintResponse;
import com.hostelix.dto.ComplaintStatsDTO;
import com.hostelix.dto.PendingResidentDTO;
import com.hostelix.enums.ComplaintCategory;
import com.hostelix.enums.ComplaintStatus;
import com.hostelix.enums.ComplaintType;
import com.hostelix.service.ComplaintService;
import com.hostelix.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {
	private final ComplaintService complaintService;

	// --- RESIDENT ENDPOINTS ---
	@PostMapping("/resident/{residentId}")
	public ResponseEntity<ApiResponse<ComplaintResponse>> raiseTicket(@PathVariable Long residentId,
			@RequestBody ComplaintRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Complaint raised successfully",
				complaintService.raiseComplaint(residentId, request)));
	}

	@GetMapping("/resident/{residentId}/history")
	public ResponseEntity<ApiResponse<Page<ComplaintResponse>>> getResidentHistory(@PathVariable Long residentId,
			@RequestParam(required = false) ComplaintStatus status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(new ApiResponse<>(200, "Resident history fetched",
				complaintService.getResidentComplaintHistory(residentId, status, PageRequest.of(page, size))));
	}

	// --- OWNER ENDPOINTS ---
	@GetMapping("/owner/hostel/{hostelId}")
	public ResponseEntity<ApiResponse<Page<ComplaintResponse>>> getHostelComplaints(@PathVariable Long hostelId,
			@RequestParam(required = false) ComplaintStatus status, @RequestParam(required = false) ComplaintCategory category,
			@RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(new ApiResponse<>(200, "Hostel complaints fetched",
				complaintService.getComplaintsForHostel(hostelId, status, category, search, PageRequest.of(page, size))));
	}

	@GetMapping("/owner/hostel/{hostelId}/pending-residents")
	public ResponseEntity<ApiResponse<List<PendingResidentDTO>>> getPendingResidents(@PathVariable Long hostelId) {
		return ResponseEntity.ok(new ApiResponse<>(200, "Pending residents list fetched",
				complaintService.getResidentsWithPendingIssues(hostelId)));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(@PathVariable Long id,
			@RequestParam ComplaintStatus status, @RequestParam(required = false) String notes) {
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Ticket status updated", complaintService.updateStatus(id, status, notes)));
	}

	@GetMapping("/stats/{hostelId}")
	public ResponseEntity<ApiResponse<ComplaintStatsDTO>> getStats(@PathVariable Long hostelId) {
		return ResponseEntity.ok(new ApiResponse<>(200, "Stats fetched", complaintService.getStats(hostelId)));
	}
}
