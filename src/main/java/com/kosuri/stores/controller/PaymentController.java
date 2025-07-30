package com.kosuri.stores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.handler.PaymentService;
import com.kosuri.stores.model.dto.OrderRequest;
import com.kosuri.stores.model.dto.PaymentRequest;
import com.kosuri.stores.model.dto.UserPaymentRequestDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping("/order")
	public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
		return new ResponseEntity<>(paymentService.createOrder(orderRequest), HttpStatus.OK);
	}

	@PostMapping("/capture")
	public ResponseEntity<?> capturePayment(@RequestBody PaymentRequest paymentRequest) {
		return new ResponseEntity<>(paymentService.capturePayment(paymentRequest), HttpStatus.OK);
	}

	@PostMapping("/save-payment-detail")
	public ResponseEntity<Void> savePaymentDetails(@Valid @RequestBody UserPaymentRequestDto savePaymentRequest) {
		paymentService.savePaymentDetails(savePaymentRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}


}
