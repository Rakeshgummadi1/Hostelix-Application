package com.hostelix.dto.pgconfig;

import java.util.List;

import com.hostelix.dto.FloorConfigRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HostelConfigRequest {

	@NotBlank(message = "Property Name is required")
	private String name;
	
	@NotBlank(message = "Location is Required")
	private String location;
	
	private List<FloorConfigRequest> floors;
}
