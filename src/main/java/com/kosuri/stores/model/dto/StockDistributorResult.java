package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class StockDistributorResult {
	private StoreDistributor store;
	private List<StockDistributor> stock;
	public StockDistributorResult(StoreDistributor store, List<StockDistributor> stock) {
		super();
		this.store = store;
		this.stock = stock;
	}
}
