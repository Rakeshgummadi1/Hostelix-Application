package com.hostelix.service;

import java.util.Map;

import com.razorpay.RazorpayException;

public interface RazorpayService {

	 String createOrder(double amount, Long residentId, String billingMonth) throws RazorpayException;
	    boolean verifyAndSavePayment(Map<String, String> paymentData);
}
