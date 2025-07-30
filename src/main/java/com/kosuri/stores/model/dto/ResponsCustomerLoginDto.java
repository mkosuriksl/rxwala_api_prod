package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class ResponsCustomerLoginDto {
	private String message;
	private boolean status;
	private String jwtToken;
	private CustomerLoginDto1 loginDetails;
	
	
}