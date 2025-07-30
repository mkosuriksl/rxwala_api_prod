package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class StoreResponseDTO {
    private String type;
    private String userIdStoreId;
    private String id;
    private String name;
    private String pincode;
    private String district;
    private String state;
    private String location;
    private String owner;
    private String ownerContact;
    private String secondaryContact;
    private String ownerEmail;
    private String storeBusinessType;
    private String userId;
}
