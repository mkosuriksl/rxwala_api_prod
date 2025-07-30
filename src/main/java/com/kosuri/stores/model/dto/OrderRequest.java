package com.kosuri.stores.model.dto;

import java.util.Map;

import lombok.Data;

@Data
public class OrderRequest {
	private String orderId;
	private int amount;
	private String receipt;
	private Map<String, String> notes;

}
