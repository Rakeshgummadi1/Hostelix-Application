package com.hostelix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentStatsDTO {
	private Double monthlyRevenue;
	private Double pendingDues;
	private Long paidResidentsCount;
}
