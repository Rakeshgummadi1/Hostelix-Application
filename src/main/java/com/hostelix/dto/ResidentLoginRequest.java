package com.hostelix.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentLoginRequest {

	@NotBlank(message = "Phone Number is Required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be 10 digits and should start with 6,7,8,9 only")
	private String phoneNumber;
}
