package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class WalkInCustomerRequestDto {
	private String name;
	private String email;
	private String phoneNumber;
	private String password;
	private String address;
	private String location;
	private String storeId; 
}
