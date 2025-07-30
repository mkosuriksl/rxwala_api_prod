package com.kosuri.stores.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class StoreAdminBrandRequest {
	
	private String brandName;

	private String brandId;

	private String storeId;

	private String itemCategory;

	private String itemSubcategory;
}


