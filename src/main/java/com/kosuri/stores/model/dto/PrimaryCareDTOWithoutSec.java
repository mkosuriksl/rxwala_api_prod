package com.kosuri.stores.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrimaryCareDTOWithoutSec {

	private String userIdStoreIdServiceId;
	private String serviceId;
	private String serviceName;
	private double price;
	private String description;
	private String userId;
	private String userIdStoreId;
	private String serviceCategory;
	public PrimaryCareDTOWithoutSec(String userIdStoreIdServiceId, String serviceId, String serviceName, double price,
			String description, String userId, String userIdStoreId, String serviceCategory) {
		super();
		this.userIdStoreIdServiceId = userIdStoreIdServiceId;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.price = price;
		this.description = description;
		this.userId = userId;
		this.userIdStoreId = userIdStoreId;
		this.serviceCategory = serviceCategory;
	}
	
	
}
