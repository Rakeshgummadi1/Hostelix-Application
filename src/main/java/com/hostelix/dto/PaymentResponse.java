package com.hostelix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.hostelix.enums.PaymentMethod;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
	private Long paymentId;
	private String transactionId;
	private Double amount;
	private String billingMonth;
	private LocalDateTime paymentDate;
	private String status;
	private PaymentMethod paymentMethod;
	private String residentName;
	private String roomNumber;
}