package com.kosuri.stores.model.request;

import lombok.Data;

@Data
public class DeleteStoreIdRequest {
	private String storeId;
	private String reason;

}
