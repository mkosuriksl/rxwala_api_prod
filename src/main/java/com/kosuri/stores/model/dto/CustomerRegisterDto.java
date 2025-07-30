package com.kosuri.stores.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerRegisterDto {
    private String cId;
    private String name;
    private String email;
    private String phoneNumber;
	private String userType;
    private String location;
	private Date regDate;
	private Date updatedDate;
	private String address;
	private String registerMode;
	private String updatedBy;
	private String storeId;

}
