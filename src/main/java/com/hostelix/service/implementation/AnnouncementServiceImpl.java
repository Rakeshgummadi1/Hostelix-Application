package com.hostelix.service.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hostelix.dto.AnnouncementRequest;
import com.hostelix.dto.AnnouncementResponse;
import com.hostelix.exceptions.ResourceNotFoundException;
import com.hostelix.model.Announcement;
import com.hostelix.model.Floor;
import com.hostelix.model.Hostel;
import com.hostelix.model.Resident;
import com.hostelix.repository.AnnouncementRepository;
import com.hostelix.repository.FloorRepository;
import com.hostelix.repository.HostelRepository;
import com.hostelix.repository.ResidentRepository;
import com.hostelix.service.AnnouncementService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
	private final AnnouncementRepository announcementRepository;
	private final HostelRepository hostelRepository;
	private final ResidentRepository residentRepository;
	private final FloorRepository floorRepository;

	@Override
	public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
		Hostel hostel = hostelRepository.findById(request.getHostelId())
				.orElseThrow(() -> new ResourceNotFoundException("Hostel not found"));
		Announcement announcement = Announcement.builder().title(request.getTitle())
				.description(request.getDescription()).type(request.getType()).target(request.getTarget())
				.floorId(request.getFloorId()).roomId(request.getRoomId()).residentId(request.getResidentId())
				.hostel(hostel).build();
		announcementRepository.save(announcement);
		return mapToResponse(announcement);
	}

	@Override
	public List<AnnouncementResponse> getResidentAnnouncements(Long residentId) {
		Resident resident = residentRepository.findById(residentId)
				.orElseThrow(() -> new ResourceNotFoundException("Resident not found"));

		Long hostelId = resident.getHostel().getHostelId();
		Long floorId = resident.getBed().getRoom().getFloor().getFloorId();
		Long roomId = resident.getBed().getRoom().getRoomId();
		return announcementRepository.findRelevantAnnouncements(hostelId, floorId, roomId, residentId).stream()
				.map(this::mapToResponse).collect(Collectors.toList());
	}

	private AnnouncementResponse mapToResponse(Announcement a) {
		String floorName = (a.getFloorId() != null)
				? floorRepository.findById(a.getFloorId()).map(Floor::getFloorName).orElse("N/A")
				: "All Floors";

		return AnnouncementResponse.builder().id(a.getAnnouncementId()).title(a.getTitle())
				.description(a.getDescription()).type(a.getType()).target(a.getTarget()).floorName(floorName)
				.date(a.getCreatedAt()).build();
	}

	@Override
	public List<AnnouncementResponse> getHostelAnnouncements(Long hostelId) {
		// Fetch all announcements for the owner's hostel, ordered by most recent
		return announcementRepository.findByHostelHostelIdOrderByCreatedAtDesc(hostelId).stream()
				.map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public void deleteAnnouncement(Long id) {
		// Check if exists before deletion to handle 404 cases
		if (!announcementRepository.existsById(id)) {
			throw new ResourceNotFoundException("Announcement not found with id: " + id);
		}
		announcementRepository.deleteById(id);
	}
}
