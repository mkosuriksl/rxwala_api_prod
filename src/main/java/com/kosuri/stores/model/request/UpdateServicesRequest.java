package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateServicesRequest {

	private String userIdStoreIdServiceId;
	private String serviceId;
	private String serviceName;
	private String storeId;
	private double price;

}
