package com.hostelix.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private Double amount;
    private LocalDate paymentDate;
    private Long residentId; // Added for frontend linking
    private String transactionId;
    private String paymentStatus;
    private String paymentMethod;
    private String billingMonth;
    
    // Flattened Resident/Room info for UI
    private String residentName;
    private String roomNumber;
}
