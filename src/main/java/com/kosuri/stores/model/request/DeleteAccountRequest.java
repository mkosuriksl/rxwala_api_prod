package com.kosuri.stores.model.request;

import lombok.Data;

@Data
public class DeleteAccountRequest {
	private String userId;
	private String reason;

}
