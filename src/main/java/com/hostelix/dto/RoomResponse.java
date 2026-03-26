package com.hostelix.dto;

import com.hostelix.enums.RoomType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomResponse {

	private Long roomId;
	private String roomNumber;
	private RoomType roomType;
}
