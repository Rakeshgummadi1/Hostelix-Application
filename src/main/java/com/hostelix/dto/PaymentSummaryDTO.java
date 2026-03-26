package com.hostelix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentSummaryDTO {
    private double amountDue;
    private String status; // PAID, PENDING
    private String dueDate;
    private String billingMonth;
}
