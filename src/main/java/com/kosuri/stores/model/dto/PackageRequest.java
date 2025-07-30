package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PackageRequest {

    private String packageId;
    private String packageName;
    private List<ServiceRequest> selectedServices;
	public PackageRequest(String packageId, String packageName, List<ServiceRequest> selectedServices) {
		super();
		this.packageId = packageId;
		this.packageName = packageName;
		this.selectedServices = selectedServices;
	}
    
    
}
