package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class UpdateRetailerOrderRequestDto {

	private String orderId;
    private List<DistributorRetailerOrderDetailsDto> orderDetailsList;
}
