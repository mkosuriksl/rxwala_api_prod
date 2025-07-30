package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.ItemCodeMaster;

import lombok.Data;

@Data
public class ResponseGetItemCodeMasterDto {
	private String message;
	private boolean status;
	private List<ItemCodeMaster> itemCodeMasters;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
