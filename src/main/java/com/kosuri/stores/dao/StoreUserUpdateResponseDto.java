package com.kosuri.stores.dao;

import java.util.Date;

import lombok.Data;

@Data
public class StoreUserUpdateResponseDto {
    private String suUserId;
    private String userIdstoreId;
    private String storeId;
    private String updatedBy;
    private Date updatedDate;
    private String username;
    private String storeUserContact;
    private String storeUserEmail;
    private String userType;
    private String status;
    private String addedBy;
}
