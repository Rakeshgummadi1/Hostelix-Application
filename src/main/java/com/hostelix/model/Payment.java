package com.hostelix.model;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;

import com.hostelix.enums.PaymentMethod;
import com.hostelix.enums.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payments")
@SQLDelete(sql = "UPDATE payments SET is_deleted = true WHERE id = ?")
public class Payment extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Double amount;
	private LocalDate paymentDate;
	private String transactionId;
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod; // NEW: To match UI (UPI, CASH, etc.)
	private String billingMonth; // NEW: format "MM-YYYY" (e.g., "03-2026")
	// This tracks which month the payment is for, essential for "Pending Dues"
	// logic.

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus; // SUCCESS, PENDING, FAILED

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "resident_id")
	private Resident resident;

}
