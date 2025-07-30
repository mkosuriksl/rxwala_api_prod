package com.kosuri.stores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.handler.CustomerPaymentService;
import com.kosuri.stores.model.dto.OrderRequest;
import com.kosuri.stores.model.dto.UserPaymentRequestDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payment")
public class CustomerPaymentController {

	@Autowired
	private CustomerPaymentService customerPaymentService;

	@PostMapping("/customer/order")
	public ResponseEntity<?> createCustomerOrder(@RequestBody OrderRequest orderRequest) {
		return new ResponseEntity<>(customerPaymentService.createCustomerOrder(orderRequest), HttpStatus.OK);
	}

	@PostMapping("/customer/save-payment-detail")
	public ResponseEntity<Void> saveCustomerPaymentDetails(
			@Valid @RequestBody UserPaymentRequestDto savePaymentRequest) {
		customerPaymentService.saveCustomerPaymentDetails(savePaymentRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
