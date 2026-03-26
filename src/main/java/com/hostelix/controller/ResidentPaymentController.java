package com.hostelix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.dto.PaymentResponse;
import com.hostelix.service.PaymentService;
import com.hostelix.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resident/payments")
@RequiredArgsConstructor
public class ResidentPaymentController {
	private final PaymentService paymentService;

	/**
	 * Get payment history for the logged-in resident
	 */
	@GetMapping("/history/{residentId}")
	public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyHistory(@PathVariable Long residentId) {
		List<PaymentResponse> history = paymentService.getResidentPaymentHistory(residentId);
		return ResponseEntity.ok(new ApiResponse<>(200, "Payment history fetched successfully", history));
	}
}
