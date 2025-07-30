package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdatePrimaryCareUserRequest {

	private String userIdStoreIdServiceId;
	private String name;
	private String location;
	private String businessName;
	private String address;
	private String phoneNumber;
	private String email;
	private String serviceName;
	private String serviceCategory;
	private String serviceId;
	private String status;
	private String pinCode;
	private double price;
	private String description;
	private String storeId;
	private String priceUpdatedBy;

}
