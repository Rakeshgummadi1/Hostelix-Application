package com.hostelix.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.hostelix.dto.PaymentRecordRequest;
import com.hostelix.dto.PaymentResponse;
import com.hostelix.dto.PaymentResponseDTO;
import com.hostelix.dto.PaymentStatsDTO;
import com.hostelix.enums.PaymentStatus;

public interface PaymentService {
	Page<PaymentResponseDTO> getPayments(Long hostelId, String search, PaymentStatus status, String month, int page,
			int size);

	PaymentStatsDTO getPaymentStats(Long hostelId, String currentMonth);

	PaymentResponseDTO recordPayment(PaymentRecordRequest request);
	
	List<PaymentResponse> getResidentPaymentHistory(Long residentId);
}
