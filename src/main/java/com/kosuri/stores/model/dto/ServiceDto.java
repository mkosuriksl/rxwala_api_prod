package com.kosuri.stores.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDto {
    private String serviceId;
    private double amount;
    private int discount;
    private String serviceName;
    private String updatedBy;
    private String packageIdLineId;
	public ServiceDto(String serviceId, double amount, int discount, String serviceName, String updatedBy,
			String packageIdLineId) {
		super();
		this.serviceId = serviceId;
		this.amount = amount;
		this.discount = discount;
		this.serviceName = serviceName;
		this.updatedBy = updatedBy;
		this.packageIdLineId = packageIdLineId;
	}
	public ServiceDto() {
		// TODO Auto-generated constructor stub
	}
    
    
}