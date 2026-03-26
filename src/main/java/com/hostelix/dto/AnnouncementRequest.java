package com.hostelix.dto;

import com.hostelix.enums.AnnouncementTarget;
import com.hostelix.enums.AnnouncementType;

import lombok.Data;

@Data
public class AnnouncementRequest {
	private String title;
	private String description;
	private AnnouncementType type;
	private AnnouncementTarget target;
	private Long floorId;
	private Long roomId;
	private Long residentId;
	private Long hostelId;
}