package com.hostelix.service;

import com.hostelix.dto.ResidentDashboardDTO;

public interface ResidentDashboardService {
	ResidentDashboardDTO getDashboardSummary(Long residentId);
}
