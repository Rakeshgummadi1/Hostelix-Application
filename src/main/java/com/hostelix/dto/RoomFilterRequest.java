package com.hostelix.dto;

import lombok.Data;

@Data
public class RoomFilterRequest {

	private String search;
	private String roomType;
	private String status;
}
