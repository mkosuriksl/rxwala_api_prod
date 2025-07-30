package com.kosuri.stores.dao;

import java.util.List;

import lombok.Data;

@Data
public class ResponseGetPrescriptionDto {
	private String message;
	private boolean status;
	private List<Prescription> prescription;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
