package com.hostelix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResidentProfileDTO {
    // Personal
    private Long residentId;
    private String fullName;
    private String email;
    private String phone;
    private String aadharNumber;
    // Stay
    private String hostelName;
    private String hostelLocation;
    private String roomNumber;
    private String floorName;
    private String sharingType;
    private Double monthlyRent;
    private String joiningDate;
}
