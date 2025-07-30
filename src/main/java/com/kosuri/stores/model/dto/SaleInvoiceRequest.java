package com.kosuri.stores.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SaleInvoiceRequest {
	private String storeId;
	private String custCode;
	private String custName;
	private Date date;
	private String doc_Number;
	private List<ItemSaleDetailRequest>detailRequests;
}
