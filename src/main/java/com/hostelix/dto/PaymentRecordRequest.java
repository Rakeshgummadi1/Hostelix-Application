package com.hostelix.dto;

import com.hostelix.enums.PaymentMethod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRecordRequest {
	@NotNull(message = "Resident ID is required")
	private Long residentId;
	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be positive")
	private Double amount;
	@NotBlank(message = "Billing month is required")
	private String billingMonth; // "MM-YYYY"
	@NotNull(message = "Payment method is required")
	private PaymentMethod paymentMethod;
	private String transactionId; // Optional for Cash
}
