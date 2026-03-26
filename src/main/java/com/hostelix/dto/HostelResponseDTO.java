package com.hostelix.dto;

import java.util.List;

import lombok.Data;

@Data
public class HostelResponseDTO {

	private Long hostelId;
	private String name;
	private String location;
	private List<FloorResponseDTO> floors;
}
