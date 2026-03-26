package com.hostelix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StayInfoDTO {
    private String roomNumber;
    private String floorName;
    private String sharingType;
    private String hostelName;
}
