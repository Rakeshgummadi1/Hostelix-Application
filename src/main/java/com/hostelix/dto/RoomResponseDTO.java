package com.hostelix.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDTO {

	private Long roomId;
	private String roomNumber;
	private String roomType;
	
	private List<BedResponseDTO> beds;
	
	private long totalBedCount;
	private long occupiedBedCount;
	private String status;
	
}
