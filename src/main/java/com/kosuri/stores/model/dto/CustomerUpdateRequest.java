package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class CustomerUpdateRequest {
    private String customerId;
    private String name;
    private String address;
}

