package com.hostelix.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentListResponse {
    private Long residentId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String aadharNumber;
    private String roomNumber;
    private String bedNumber;
    private Double monthlyFees;
    private LocalDate joiningDate;
    private Integer sharing; // Calculated from Room capacity
}
