package com.hostelix.service.implementation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hostelix.dto.PaymentRecordRequest;
import com.hostelix.dto.PaymentResponse;
import com.hostelix.dto.PaymentResponseDTO;
import com.hostelix.dto.PaymentStatsDTO;
import com.hostelix.enums.PaymentStatus;
import com.hostelix.exceptions.ResourceNotFoundException;
import com.hostelix.model.Payment;
import com.hostelix.model.Resident;
import com.hostelix.repository.PaymentRepository;
import com.hostelix.repository.ResidentRepository;
import com.hostelix.service.PaymentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImplementation implements PaymentService {
	private final PaymentRepository paymentRepository;
	private final ResidentRepository residentRepository;

	public Page<PaymentResponseDTO> getPayments(Long hostelId, String search, PaymentStatus status, String month,
			int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return paymentRepository.findFilteredPayments(hostelId, search, status, month, pageable).map(this::mapToDTO);
	}

	public PaymentStatsDTO getPaymentStats(Long hostelId, String currentMonth) {
		Double revenue = paymentRepository.sumMonthlyRevenue(hostelId, currentMonth);
		Long paidCount = paymentRepository.countPaidResidents(hostelId, currentMonth);

		// Logic for Pending Dues:
		// sum(monthlyFees) of residents who do NOT have a SUCCESS payment for this
		// month
		Double pending = residentRepository.calculatePendingDues(hostelId, currentMonth);
		return PaymentStatsDTO.builder().monthlyRevenue(revenue != null ? revenue : 0.0)
				.paidResidentsCount(paidCount != null ? paidCount : 0).pendingDues(pending != null ? pending : 0.0)
				.build();
	}

	@Override
	@Transactional
	public PaymentResponseDTO recordPayment(PaymentRecordRequest request) {
		// 1. Check for an existing PENDING record for this month
		Payment payment = paymentRepository.findByResidentResidentIdAndBillingMonthAndPaymentStatus(
				request.getResidentId(), request.getBillingMonth(), PaymentStatus.PENDING).orElse(null);
		if (payment != null) {
			// RECONCILIATION: Update the PENDING record
			payment.setAmount(request.getAmount());
			payment.setPaymentMethod(request.getPaymentMethod());
			payment.setTransactionId(request.getTransactionId());
			payment.setPaymentStatus(PaymentStatus.SUCCESS);
			payment.setPaymentDate(LocalDate.now());
		} else {
			// FALLBACK: Create new SUCCESS record
			Resident resident = residentRepository.findById(request.getResidentId())
					.orElseThrow(() -> new RuntimeException("Resident not found with ID: " + request.getResidentId()));
			payment = new Payment();
			payment.setResident(resident);
			payment.setAmount(request.getAmount());
			payment.setBillingMonth(request.getBillingMonth());
			payment.setPaymentMethod(request.getPaymentMethod());
			payment.setTransactionId(request.getTransactionId());
			payment.setPaymentStatus(PaymentStatus.SUCCESS);
			payment.setPaymentDate(LocalDate.now());
		}
		Payment saved = paymentRepository.save(payment);
		return mapToDTO(saved);
	}

	   private PaymentResponseDTO mapToDTO(Payment p) {
	        return PaymentResponseDTO.builder()
	                .id(p.getId())
	                .amount(p.getAmount())
	                .residentId(p.getResident().getResidentId())
	                .paymentDate(p.getPaymentDate())
	                .transactionId(p.getTransactionId())
	                .paymentStatus(p.getPaymentStatus().name())
	                .paymentMethod(p.getPaymentMethod() != null ? p.getPaymentMethod().name() : "N/A")
	                .billingMonth(p.getBillingMonth())
	                .residentName(p.getResident().getFullName())
	                .roomNumber(p.getResident().getBed().getRoom().getRoomNumber())
	                .build();
	    }
	   
	   @Override
	   public List<PaymentResponse> getResidentPaymentHistory(Long residentId) {
	       // Check if resident exists
	       if (!residentRepository.existsById(residentId)) {
	           throw new ResourceNotFoundException("Resident not found");
	       }
	       List<Payment> payments = paymentRepository.findByResidentResidentIdOrderByCreatedAtDesc(residentId);
	       
	       return payments.stream()
	               .map(this::mapToResponseDTO)
	               .collect(Collectors.toList());
	   }
	   /**
	    * Helper to map Payment entity to Response DTO
	    */
	   private PaymentResponse mapToResponseDTO(Payment p) {
	       return PaymentResponse.builder()
	               .paymentId(p.getId())
	               .transactionId(p.getTransactionId())
	               .amount(p.getAmount())
	               .billingMonth(p.getBillingMonth())
	               .paymentDate(p.getCreatedAt())
	               .status(p.getPaymentStatus().name())
	               .paymentMethod(p.getPaymentMethod())
	               .residentName(p.getResident().getFullName())
	               .roomNumber(p.getResident().getBed().getRoom().getRoomNumber())
	               .build();
	   }
}