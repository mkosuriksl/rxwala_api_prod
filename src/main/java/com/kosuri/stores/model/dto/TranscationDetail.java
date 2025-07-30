package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class TranscationDetail {
	private String orderId;
	private String currency;
	private long amount;
	public TranscationDetail( String orderId, String currency, long amount) {
		super();
		this.orderId = orderId;
		this.currency = currency;
		this.amount = amount;
	}

	

}
