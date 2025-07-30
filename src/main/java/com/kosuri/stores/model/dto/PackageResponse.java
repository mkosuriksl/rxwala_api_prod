package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class PackageResponse {
	private String packageId;
	private String packageName;
	private Double totalAmount;
	private String serviceRequestId; 
	private String serviceRequestIdLineId;
	private List<ServiceResponse> selectedServices;
	public PackageResponse(String packageId, String packageName, Double totalAmount, String serviceRequestId,
			String serviceRequestIdLineId, List<ServiceResponse> selectedServices) {
		super();
		this.packageId = packageId;
		this.packageName = packageName;
		this.totalAmount = totalAmount;
		this.serviceRequestId = serviceRequestId;
		this.serviceRequestIdLineId = serviceRequestIdLineId;
		this.selectedServices = selectedServices;
	}
	
	
}
