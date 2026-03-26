package com.hostelix.dto;

import lombok.Data;

@Data
public class OwnerResponseDTO {

	private Long ownerId;
	
	private String ownerName;
	
	private String email;
}
