package com.kosuri.stores.dao;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerRegistrationInfoDto {
	private Long id;
	private String cId;
	private String name;
	private String email;
	private String phoneNumber;
	private String userType;
	private Date registeredDate;
	private Date updatedDate;
	private String registerMode;
	private String userId;
}
