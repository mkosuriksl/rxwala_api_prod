package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class CustomerOrderRequestDto {
    private String storeId;
    private List<CustomerOrderDetailsDto> orderDetailsList;
}
