package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class StockMrpUpdateRequest {
	private String userIdStoreIdItemCode;
    private String batch;
    private Double mrpValue;
    private Double mrpPack;

}
