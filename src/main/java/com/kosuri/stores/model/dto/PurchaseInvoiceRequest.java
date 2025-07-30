package com.kosuri.stores.model.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PurchaseInvoiceRequest {
	private String storeId;
	private String supplierCode;
	private String supplierName;
	private Date purchaseDate;
	private String invoiceNo;
	private String itemCategory;
	private String itemSubCategory;
	private List<ItemDetailRequest> detailRequests;

}
