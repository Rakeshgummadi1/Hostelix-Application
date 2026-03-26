package com.hostelix.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResidentDashboardDTO {
    private StayInfoDTO stayInfo;
    private PaymentSummaryDTO paymentSummary;
    private ComplaintSummaryDTO complaintSummary;
    private AnnouncementDTO latestAnnouncement;
    private List<RoommateDTO> roommates;
}
