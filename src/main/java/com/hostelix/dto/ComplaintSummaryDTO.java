package com.hostelix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplaintSummaryDTO {
    private long activeCount;
    private String latestTicketStatus;
}

