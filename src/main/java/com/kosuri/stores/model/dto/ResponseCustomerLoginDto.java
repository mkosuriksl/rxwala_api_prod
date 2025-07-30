package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class ResponseCustomerLoginDto {

	private String message;
	private boolean status;
	private CustomerRegisterDto DlerProfile;
	
}
