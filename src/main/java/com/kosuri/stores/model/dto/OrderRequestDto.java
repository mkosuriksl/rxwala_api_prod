package com.kosuri.stores.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDto {
    private String customerId;
//    private String storeId;
    private String location;
    private BigDecimal orderAmount;
    private float gstTotal;
    private String deliveryMethod;
    private String userIdstoreId;
    private List<OrderDetailsDto> orderDetailsList;

}


