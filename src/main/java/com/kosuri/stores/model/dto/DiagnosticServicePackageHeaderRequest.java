package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class DiagnosticServicePackageHeaderRequest {
	
	private String useridStoreidPackageid;
	
	private String serviceCategoryId;

	private String storeId;

	private String packageId;

	private String packageName;

	private Double amount;

	private String updatedBy;

}
