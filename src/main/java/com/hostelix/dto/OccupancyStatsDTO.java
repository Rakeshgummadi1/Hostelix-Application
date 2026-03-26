package com.hostelix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OccupancyStatsDTO {

	private long totalBeds;
	private long totalRooms;
	private long occupiedBeds;
	private long availableBeds;
	private double occupencyPercentage;
}
