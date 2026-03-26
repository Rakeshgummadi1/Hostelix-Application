package com.hostelix.service.implementation;

import java.time.LocalDate;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hostelix.enums.PaymentMethod;
import com.hostelix.enums.PaymentStatus;
import com.hostelix.model.Payment;
import com.hostelix.model.Resident;
import com.hostelix.repository.PaymentRepository;
import com.hostelix.repository.ResidentRepository;
import com.hostelix.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {
    private final PaymentRepository paymentRepository;
    private final ResidentRepository residentRepository;
    @Value("${razorpay.key.id}")
    private String keyId;
    @Value("${razorpay.key.secret}")
    private String keySecret;
    @Override
    public String createOrder(double amount, Long residentId, String billingMonth) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);
        
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amount * 100)); // paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        
        // Add metadata so we can identify the payment during verification
        JSONObject notes = new JSONObject();
        notes.put("residentId", residentId.toString());
        notes.put("billingMonth", billingMonth);
        orderRequest.put("notes", notes);
        
        Order order = client.orders.create(orderRequest);
        return order.toString();
    }
    @Override
    public boolean verifyAndSavePayment(Map<String, String> data) {
        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            
            // 1. Verify Signature
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);
            Utils.verifyPaymentSignature(attributes, keySecret);
            // 2. Fetch Order to get metadata (notes)
            Order order = client.orders.fetch(orderId);
            JSONObject notes = order.get("notes");
            Long residentId = Long.parseLong(notes.getString("residentId"));
            String billingMonth = notes.getString("billingMonth");
         // FIX: Razorpay returns amount as Integer (paise), use Number to avoid ClassCastException
            Number amountInPaise = order.get("amount");
            double amount = amountInPaise.doubleValue() / 100.0;
            // 3. Save to Database
            saveToDatabase(residentId, amount, billingMonth, paymentId);
            
            return true;
        } catch (RazorpayException e) {
            return false;
        }
    }
    private void saveToDatabase(Long residentId, Double amount, String month, String txnId) {
        // Find the existing pending payment record
        Payment payment = paymentRepository.findByResidentIdAndBillingMonth(residentId, month)
            .orElseGet(() -> {
                // Fallback: If for some reason the record doesn't exist, create it
                Resident resident = residentRepository.findById(residentId)
                    .orElseThrow(() -> new RuntimeException("Resident not found"));
                return Payment.builder()
                        .resident(resident)
                        .billingMonth(month)
                        .build();
            });
        // Update the existing record
        payment.setAmount(amount);
        payment.setTransactionId(txnId);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentMethod(PaymentMethod.ONLINE);
        payment.setPaymentDate(LocalDate.now());
        paymentRepository.save(payment);
    }
}
