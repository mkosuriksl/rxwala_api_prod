package com.kosuri.stores.model.dto;

import com.kosuri.stores.model.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderQtyUpdateDto {
    private String lineItemId;
    private int orderQty;
    private OrderStatus orderStatus;
}
