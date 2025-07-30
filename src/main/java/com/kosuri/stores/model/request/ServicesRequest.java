package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServicesRequest {

	private String serviceId;
	private String serviceName;
	private double price;


}
