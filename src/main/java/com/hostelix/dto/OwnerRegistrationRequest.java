package com.hostelix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OwnerRegistrationRequest {

	@NotBlank(message = "Owner Name is Required")
	private String ownerName;
	
	@NotBlank(message = "Email is Required")
	@Email(message = "Invalid Email Format")
	private String email;
	
	@NotBlank(message = "Password is Required")
	@Size(min = 6, message = "Password must be atleast 6 characters")
	private String password;
	
	@NotBlank(message = "Confirm Password is required")
	private String confirmPassword;
}
