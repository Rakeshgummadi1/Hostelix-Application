package com.hostelix.service;

import com.hostelix.dto.HostelResponseDTO;
import com.hostelix.dto.OccupancyStatsDTO;
import com.hostelix.dto.RoomFilterRequest;
import com.hostelix.dto.pgconfig.HostelConfigRequest;

public interface HostelService {

	HostelResponseDTO SaveConfiguration(Long ownerId, HostelConfigRequest hostelConfigRequest);
	HostelResponseDTO getHostelStatus(Long ownerId, RoomFilterRequest filters);
	OccupancyStatsDTO getOccupancyStats(Long ownerId);
	
	HostelResponseDTO getHostelConfig(Long ownerId);
}


