package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.StoreEntity;

import lombok.Data;

@Data
public class ResponseGetStoreRelatedResponseDto {
	
	private String message;
	private boolean status;
    private List<StoreEntity> stores;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;

}
