package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class StoreDetailsDto {
	private String type;
	private String userIdStoreId;
    private String storeId;
    private String name;
    private String pincode;
    private String district;
    private String state;
    private String location;
    private String owner;
    private String ownerContact;
    private String ownerEmail;
	public StoreDetailsDto(String type, String userIdStoreId, String storeId, String name, String pincode,
			String district, String state, String location, String owner, String ownerContact, String ownerEmail) {
		super();
		this.type = type;
		this.userIdStoreId = userIdStoreId;
		this.storeId = storeId;
		this.name = name;
		this.pincode = pincode;
		this.district = district;
		this.state = state;
		this.location = location;
		this.owner = owner;
		this.ownerContact = ownerContact;
		this.ownerEmail = ownerEmail;
	}
    
    
}
