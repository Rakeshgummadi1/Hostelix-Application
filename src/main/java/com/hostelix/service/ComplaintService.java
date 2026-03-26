package com.hostelix.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hostelix.dto.ComplaintRequest;
import com.hostelix.dto.ComplaintResponse;
import com.hostelix.dto.ComplaintStatsDTO;
import com.hostelix.dto.PendingResidentDTO;
import com.hostelix.enums.ComplaintCategory;
import com.hostelix.enums.ComplaintStatus;
import com.hostelix.enums.ComplaintType;

public interface ComplaintService {
	// Resident Operations
	ComplaintResponse raiseComplaint(Long residentId, ComplaintRequest request);

	Page<ComplaintResponse> getResidentComplaintHistory(Long residentId, ComplaintStatus status, Pageable pageable);

	// Owner Operations
	Page<ComplaintResponse> getComplaintsForHostel(Long hostelId, ComplaintStatus status, ComplaintCategory category,
			String search, Pageable pageable);

	ComplaintResponse updateStatus(Long id, ComplaintStatus status, String notes);

	ComplaintStatsDTO getStats(Long hostelId);

	List<PendingResidentDTO> getResidentsWithPendingIssues(Long hostelId);
}
