package com.kosuri.stores.model.response;

import lombok.Data;

@Data
public class MaterialDetailResponse {
	private String storeId;
	private String itemName;
	private String itemCategory;
	private String mrp;
	private String brand;
	private String batchNumber;
	private String expiryDate;

}
