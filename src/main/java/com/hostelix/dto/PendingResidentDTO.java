package com.hostelix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingResidentDTO {
    private Long residentId;
    private String residentName;
    private String roomNumber;
    private long pendingCount;
}