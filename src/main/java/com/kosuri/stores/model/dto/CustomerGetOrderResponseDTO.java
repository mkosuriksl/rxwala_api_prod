package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class CustomerGetOrderResponseDTO {
	private String orderId;
	private String storeId;
	private List<CustomerOrderDetailsDto> orderDetailsList;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
