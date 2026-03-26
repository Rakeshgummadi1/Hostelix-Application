package com.hostelix.dto;

import java.time.LocalDateTime;

import com.hostelix.enums.AnnouncementTarget;
import com.hostelix.enums.AnnouncementType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnnouncementResponse {
	private Long id;
	private String title;
	private String description;
	private AnnouncementType type;
	private AnnouncementTarget target;
	private String floorName;
	private LocalDateTime date;
}