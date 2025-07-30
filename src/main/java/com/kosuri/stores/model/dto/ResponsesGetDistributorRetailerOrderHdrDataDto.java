package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.DistributorRetailerOrderHdrEntity;

import lombok.Data;
@Data
public class ResponsesGetDistributorRetailerOrderHdrDataDto {
	private String message;
	private boolean status;
	private List<DistributorRetailerOrderHdrEnrichedDto> distributorRetailerOrderHdrData;
	private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}