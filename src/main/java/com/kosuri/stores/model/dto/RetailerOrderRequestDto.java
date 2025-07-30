package com.kosuri.stores.model.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class RetailerOrderRequestDto {

    private String retailerId;
    private String storeId;
    private String invoiceNo;
//    private String location;
    private String distrubutorId;
    private BigDecimal orderAmount;
//    private float gstTotal;
//    private String retailerPhoneNumber;
    private List<DistributorRetailerOrderDetailsDto> orderDetailsList;
}
