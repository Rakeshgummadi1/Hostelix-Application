package com.hostelix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplaintStatsDTO {
    private long openTickets;
    private long inProgress;
    private long resolved;
}
