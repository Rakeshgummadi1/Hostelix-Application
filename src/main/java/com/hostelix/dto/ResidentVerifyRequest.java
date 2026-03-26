package com.hostelix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResidentVerifyRequest {
	@NotBlank
	private String phoneNumber;
	@NotBlank
	@Size(min = 6, max = 6, message = "OTP must be 6 digits")
	private String otp;
}
