package com.kosuri.stores.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CustomerDetailsDto {	
	private Long id;
	private String cId;
	private String name;
	private String email;
	private String phoneNumber;
	private String location;
	private Date registeredDate;
	private Date updatedDate;
	public CustomerDetailsDto(Long id, String cId, String name, String email, String phoneNumber, String location,
			Date registeredDate, Date updatedDate) {
		super();
		this.id = id;
		this.cId = cId;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.location = location;
		this.registeredDate = registeredDate;
		this.updatedDate = updatedDate;
	}

}
