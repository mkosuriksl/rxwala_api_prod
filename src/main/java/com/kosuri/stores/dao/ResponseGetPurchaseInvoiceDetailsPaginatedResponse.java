package com.kosuri.stores.dao;

import java.util.List;

import lombok.Data;

@Data
public class ResponseGetPurchaseInvoiceDetailsPaginatedResponse {
	private boolean status;
    private String message;
    private List<PurchaseEntity> purchases;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
