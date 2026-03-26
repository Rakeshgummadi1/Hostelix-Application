package com.hostelix.service;

import java.util.List;

import com.hostelix.dto.AnnouncementRequest;
import com.hostelix.dto.AnnouncementResponse;

public interface AnnouncementService {
	AnnouncementResponse createAnnouncement(AnnouncementRequest request);

	List<AnnouncementResponse> getHostelAnnouncements(Long hostelId);

	List<AnnouncementResponse> getResidentAnnouncements(Long residentId);

	void deleteAnnouncement(Long id);
}
