package com.hostelix.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostelix.service.RazorpayService;
import com.hostelix.util.ApiResponse;
import com.razorpay.RazorpayException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments/razorpay")
@RequiredArgsConstructor
public class RazorpayController {
    
    private final RazorpayService razorpayService;
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<String>> createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
        double amount = Double.parseDouble(data.get("amount").toString());
        Long residentId = Long.parseLong(data.get("residentId").toString());
        String billingMonth = data.get("billingMonth").toString();
        String orderJson = razorpayService.createOrder(amount, residentId, billingMonth);
        return ResponseEntity.ok(new ApiResponse<>(200, "Order created", orderJson));
    }
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyPayment(@RequestBody Map<String, String> data) {
        boolean isSaved = razorpayService.verifyAndSavePayment(data);
        if (isSaved) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Payment verified and saved", true));
        } else {
            return ResponseEntity.status(400).body(new ApiResponse<>(400, "Verification failed", false));
        }
    }
}
