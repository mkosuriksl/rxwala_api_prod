package com.kosuri.stores.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDto1 {
    private String serviceId;
    private double amount;
    private int discount;
    private String serviceName;
    private String packageIdLineId;
	public ServiceDto1(String serviceId, double amount, int discount, String serviceName,
			String packageIdLineId) {
		super();
		this.serviceId = serviceId;
		this.amount = amount;
		this.discount = discount;
		this.serviceName = serviceName;
		this.packageIdLineId = packageIdLineId;
	}
	public ServiceDto1() {
		// TODO Auto-generated constructor stub
	}
    
    
}