package com.kosuri.stores.dao;

import lombok.Data;

@Data
public class StoreBasicInfoDto {
    private String id;
    private String name;
    private String location;
    private String ownerContact;
    private String ownerEmail;
    private String userId;
}