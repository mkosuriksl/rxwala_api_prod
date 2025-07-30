package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageRequestDto1 {
	private String packageId;
	private String packageName;
	private double totalAmount;
	private String userIdStoreId;
	private List<ServiceDto1> selectedServices;
	public PackageRequestDto1(String packageId, String packageName, double totalAmount,String userIdStoreId,
	List<ServiceDto1> selectedServices) {
		super();
		this.packageId = packageId;
		this.packageName = packageName;
		this.totalAmount = totalAmount;
		this.userIdStoreId=userIdStoreId;
		this.selectedServices = selectedServices;
	}
	
}