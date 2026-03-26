package com.hostelix.dto;

import com.hostelix.enums.ComplaintCategory;
import com.hostelix.enums.ComplaintPriority;
import com.hostelix.enums.ComplaintType;

import lombok.Data;

@Data
public class ComplaintRequest {
	private String title;
	private String description;
	private ComplaintCategory type;
	private ComplaintPriority priority;
	private Long hostelId;
}
