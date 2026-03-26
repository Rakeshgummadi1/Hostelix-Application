package com.hostelix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResidentRegistrationRequest {
    @NotNull(message = "Hostel ID is required")
    private Long hostelId;
    @NotBlank(message = "Full Name is required")
    private String fullName;
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "^\\d{10}$", message = "Phone must be 10 digits")
    private String phoneNumber;
    @Pattern(regexp = "^\\d{12}$", message = "Aadhar must be 12 digits")
    private String aadharNumber;
    @NotNull(message = "Bed ID is required")
    private Long bedId;
    @NotNull(message = "Monthly fees is required")
    private Double monthlyFees; // Remember to add this field to your Resident Entity later!
}
