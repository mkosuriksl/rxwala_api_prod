package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.DistributorRetailerOrderDetailsEntity;

import lombok.Data;

@Data
public class ResponseGetDistributorRetailerOrderDetailsEntityDto {
	private String message;
	private boolean status;
	private List<DistributorRetailerOrderDetailsEntity> distributorRetailerOrderDetailsEntity;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}