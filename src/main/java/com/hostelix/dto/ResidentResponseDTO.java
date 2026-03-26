package com.hostelix.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentResponseDTO {

	private Long residentId;
	private String fullName;
	private String phoneNumber;
	private String email;
	private String aadharNumber;
	private Double monthlyFees;
	private LocalDate joiningDate;
}
