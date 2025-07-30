package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class DiagnosticServicePackageLineRequest {

	private String serviceCategoryId;

	private String storeId;

	private String packageId;

	private String packageName;

	private String serviceId;

	private Double amount;

	private int discount;

	private String updatedBy;

}
