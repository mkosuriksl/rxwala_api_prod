package com.kosuri.stores.model.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdatePrimaryCareServicesRequest {

	private String storeName;
	private List<UpdateServicesRequest> services;

}
