package com.kosuri.stores.dao;

import java.util.List;

import lombok.Data;

@Data
public class ResponseGetPrescriptionHistoryDto {
	private String message;
	private boolean status;
	private List<PrescriptionHistory> prescriptionHistory;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
