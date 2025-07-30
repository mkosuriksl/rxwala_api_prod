package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateDiagnosticCenterData {
	
	private String serviceId;
	private String serviceName;
	private String price;
	private String userServiceId;
	
}
