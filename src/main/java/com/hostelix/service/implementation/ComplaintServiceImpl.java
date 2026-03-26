package com.hostelix.service.implementation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hostelix.dto.ComplaintRequest;
import com.hostelix.dto.ComplaintResponse;
import com.hostelix.dto.ComplaintStatsDTO;
import com.hostelix.dto.PendingResidentDTO;
import com.hostelix.enums.ComplaintCategory;
import com.hostelix.enums.ComplaintStatus;
import com.hostelix.enums.ComplaintType;
import com.hostelix.exceptions.ResourceNotFoundException;
import com.hostelix.model.Complaint;
import com.hostelix.model.Hostel;
import com.hostelix.model.Resident;
import com.hostelix.repository.ComplaintRepository;
import com.hostelix.repository.HostelRepository;
import com.hostelix.repository.ResidentRepository;
import com.hostelix.service.ComplaintService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {
	private final ComplaintRepository complaintRepository;
	private final ResidentRepository residentRepository;
	private final HostelRepository hostelRepository;

	@Override
	@Transactional
	public ComplaintResponse raiseComplaint(Long residentId, ComplaintRequest request) {
		Resident resident = residentRepository.findById(residentId)
				.orElseThrow(() -> new ResourceNotFoundException("Resident not found"));
		Hostel hostel = hostelRepository.findById(request.getHostelId())
				.orElseThrow(() -> new ResourceNotFoundException("Hostel not found"));
		Complaint complaint = new Complaint();
		complaint.setResident(resident);
		complaint.setHostel(hostel);
		complaint.setTitle(request.getTitle());
		complaint.setDescription(request.getDescription());
		complaint.setCategory(request.getType());
		complaint.setPriority(request.getPriority());
		complaint.setStatus(ComplaintStatus.OPEN);
		complaint.setTicketId("TKT-" + (System.currentTimeMillis() % 1000000));
		return mapToDTO(complaintRepository.save(complaint));
	}

	@Override
	public Page<ComplaintResponse> getResidentComplaintHistory(Long residentId, ComplaintStatus status,
			Pageable pageable) {
		return complaintRepository.findByResidentIdAndStatus(residentId, status, pageable).map(this::mapToDTO);
	}

	@Override
	public Page<ComplaintResponse> getComplaintsForHostel(Long hostelId, ComplaintStatus status, ComplaintCategory category,
			String search, Pageable pageable) {
		return complaintRepository.findAllWithFilters(hostelId, status, category, search, pageable).map(this::mapToDTO);
	}

	@Override
	@Transactional
	public ComplaintResponse updateStatus(Long id, ComplaintStatus status, String notes) {
		Complaint complaint = complaintRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
		complaint.setStatus(status);
		if (notes != null)
			complaint.setResolutionNotes(notes);
		return mapToDTO(complaintRepository.save(complaint));
	}

	@Override
	public ComplaintStatsDTO getStats(Long hostelId) {
		return ComplaintStatsDTO.builder()
				.openTickets(complaintRepository.countByHostelHostelIdAndStatus(hostelId, ComplaintStatus.OPEN))
				.inProgress(complaintRepository.countByHostelHostelIdAndStatus(hostelId, ComplaintStatus.IN_PROGRESS))
				.resolved(complaintRepository.countByHostelHostelIdAndStatus(hostelId, ComplaintStatus.RESOLVED))
				.build();
	}

	@Override
	public List<PendingResidentDTO> getResidentsWithPendingIssues(Long hostelId) {
		return complaintRepository.findResidentsWithPendingComplaints(hostelId);
	}

	private ComplaintResponse mapToDTO(Complaint c) {
		return ComplaintResponse.builder().complaintId(c.getComplaintId()).ticketId(c.getTicketId()).title(c.getTitle())
				.description(c.getDescription()).type(c.getCategory().name())
				.priority(c.getPriority() != null ? c.getPriority().name() : "MEDIUM").status(c.getStatus().name())
				.residentName(c.getResident().getFullName()).residentId(c.getResident().getResidentId())
				.roomNumber(c.getResident().getBed().getRoom().getRoomNumber()).createdAt(c.getCreatedAt())
				.resolutionNotes(c.getResolutionNotes()).build();
	}
}
