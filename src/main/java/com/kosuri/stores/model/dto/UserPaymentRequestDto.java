package com.kosuri.stores.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserPaymentRequestDto {

	@JsonProperty("razorpay_order_id")
	private String razorPayOrderId;

	@JsonProperty("razorpay_payment_id")
	private String razorPaymentId;

	@JsonProperty("razorpay_signature")
	private String razorPaySignature;

}