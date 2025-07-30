package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class DistributorRetailerOrderResponseDTO {
	private String orderId;
	private String retailerId;
	private String storeId;
	private String invoiceNo;
	private String distributorId;
	private List<DistributorRetailerOrderDetailsDto> orderDetailsList;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
