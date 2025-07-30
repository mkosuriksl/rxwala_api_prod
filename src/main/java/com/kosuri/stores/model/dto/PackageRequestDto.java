package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageRequestDto {
	private String packageId;
	private String packageName;
	private double totalAmount;
	private String updatedBy;
	private String storeId;
	private String userId;
	private List<ServiceDto> selectedServices;
	public PackageRequestDto(String packageId, String packageName, double totalAmount,
			String updatedBy, String storeId, String userId, List<ServiceDto> selectedServices) {
		super();
		this.packageId = packageId;
		this.packageName = packageName;
		this.totalAmount = totalAmount;
		this.updatedBy = updatedBy;
		this.storeId = storeId;
		this.userId = userId;
		this.selectedServices = selectedServices;
	}
	
}