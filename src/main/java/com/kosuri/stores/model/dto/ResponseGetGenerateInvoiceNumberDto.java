package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.GenerateInvoiceEntity;

import lombok.Data;

@Data
public class ResponseGetGenerateInvoiceNumberDto {
	private String message;
	private boolean status;
	private List<GenerateInvoiceEntity> generateInvoiceEntity;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
