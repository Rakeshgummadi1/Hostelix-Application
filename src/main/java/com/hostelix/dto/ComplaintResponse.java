package com.hostelix.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplaintResponse {
	private Long complaintId;
	private String ticketId;
	private String title;
	private String description;
	private String type;
	private String priority;
	private String status;
	private String residentName;
	private Long residentId;
	private String roomNumber;
	private LocalDateTime createdAt;
	private String resolutionNotes;
}
