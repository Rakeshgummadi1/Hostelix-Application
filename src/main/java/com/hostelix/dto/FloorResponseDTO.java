package com.hostelix.dto;

import java.util.List;

import lombok.Data;

@Data
public class FloorResponseDTO {

	private Long floorId;
	private String floorName;
	private Integer floorNumber;
	private List<RoomResponseDTO> rooms;
}
