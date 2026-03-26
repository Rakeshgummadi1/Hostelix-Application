package com.hostelix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BedResponseDTO {

	private Long bedId;
	private String bedNumber;
	private Boolean isOccupied;
}
