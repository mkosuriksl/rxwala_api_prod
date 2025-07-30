package com.kosuri.stores.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreResponseDto {
    private String location;
    private String storeId;
    private String ownerContact;
    private String type;
    private String name;
}
