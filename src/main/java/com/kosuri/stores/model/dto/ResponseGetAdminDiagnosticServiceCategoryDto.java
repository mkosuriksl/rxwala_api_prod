package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.AdminDiagnosticServiceCategory;

import lombok.Data;

@Data
public class ResponseGetAdminDiagnosticServiceCategoryDto {
	private String message;
	private boolean status;
	private List<AdminDiagnosticServiceCategory> adminDignosticServiceCategory;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
