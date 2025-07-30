package com.kosuri.stores.dao;

import java.util.List;

import lombok.Data;

@Data
public class SaleUpdateRequestDto {
	private String doc_Number; // common invoice number
    private List<SaleUpdateRequestEntity> updates; // list of updates
}
