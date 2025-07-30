package com.kosuri.stores.model.response;

import java.util.List;

import com.kosuri.stores.dao.StoreEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ResponseGetStoreRelatedResponse {
	private String message;
	private boolean status;
    private List<StoreEntity> stores;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;

}
