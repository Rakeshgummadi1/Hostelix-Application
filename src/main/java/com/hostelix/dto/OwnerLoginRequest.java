package com.hostelix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OwnerLoginRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid Email Format")
	private String email;
	
	@NotBlank(message = "Password is Required")
	private String password;
}
