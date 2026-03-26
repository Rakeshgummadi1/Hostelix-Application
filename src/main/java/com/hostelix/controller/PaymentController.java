package com.hostelix.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.PaymentRecordRequest;
import com.hostelix.dto.PaymentResponse;
import com.hostelix.dto.PaymentResponseDTO;
import com.hostelix.dto.PaymentStatsDTO;
import com.hostelix.enums.PaymentStatus;
import com.hostelix.service.PaymentService;
import com.hostelix.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owner/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;

	@GetMapping("/hostel/{hostelId}")
	public ResponseEntity<Page<PaymentResponseDTO>> getPayments(@PathVariable Long hostelId,
			@RequestParam(required = false) String search, @RequestParam(required = false) PaymentStatus status,
			@RequestParam(required = false) String month, // format "MM-YYYY"
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(paymentService.getPayments(hostelId, search, status, month, page, size));
	}

	@PostMapping("/record")
	public ResponseEntity<ApiResponse<PaymentResponseDTO>> recordPayment(
			@Valid @RequestBody PaymentRecordRequest request) {

		PaymentResponseDTO data = paymentService.recordPayment(request);

		ApiResponse<PaymentResponseDTO> response = ApiResponse.<PaymentResponseDTO>builder()
				.status(HttpStatus.CREATED.value()).message("Payment recorded successfully").data(data).build();

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/stats/hostel/{hostelId}")
	public ResponseEntity<PaymentStatsDTO> getStats(@PathVariable Long hostelId, @RequestParam String currentMonth) {
		return ResponseEntity.ok(paymentService.getPaymentStats(hostelId, currentMonth));
	}

	// B. Get Specific Resident History (ADD THIS)
	@GetMapping("/resident/{residentId}")
	public ResponseEntity<ApiResponse<List<PaymentResponse>>> getResidentPaymentHistory(@PathVariable Long residentId) {
		List<PaymentResponse> history = paymentService.getResidentPaymentHistory(residentId);
		return ResponseEntity.ok(new ApiResponse<>(200, "Success", history));
	}
}
