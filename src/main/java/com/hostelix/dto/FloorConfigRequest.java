package com.hostelix.dto;


import java.util.List;

import lombok.Data;

@Data
public class FloorConfigRequest {

	private String floorName;
	
	private Integer floorNumber;
	
	private List<RoomConfigRequest> rooms;
}
