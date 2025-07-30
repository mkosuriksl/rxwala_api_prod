package com.kosuri.stores.dao;

import java.util.List;

import lombok.Data;

@Data
public class ResponseGetSaleEntityPaginatedResponse {
	private boolean status;
    private String message;
    private List<SaleEntity> sales;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
