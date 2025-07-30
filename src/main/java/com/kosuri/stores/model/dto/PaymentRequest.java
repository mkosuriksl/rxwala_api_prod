package com.kosuri.stores.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRequest {

	private String paymentId;
	private int amount;

}
