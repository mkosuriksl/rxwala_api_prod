package com.kosuri.stores.dao;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseUpdateRequestDto {
	private String invoiceNo; // common invoice number
    private List<PurchaseUpdateRequestEntity> updates; // list of updates
}
