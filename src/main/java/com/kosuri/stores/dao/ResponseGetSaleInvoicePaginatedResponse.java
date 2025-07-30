package com.kosuri.stores.dao;

import java.util.List;

import com.kosuri.stores.model.dto.SaleInvoiceResponseDto;

import lombok.Data;

@Data
public class ResponseGetSaleInvoicePaginatedResponse {
	private boolean status;
    private String message;
    private List<SaleInvoiceResponseDto> invoices;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
