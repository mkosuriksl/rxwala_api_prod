package com.kosuri.stores.dao;

import lombok.Data;

@Data
public class CustomerWalkInUpdateRequest {
    private String customerId; 
    private String email;
    private String phoneNumber;
    private String name;
    private String location;
    private String registerMode;
    private String updatedBy;
}

