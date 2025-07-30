package com.kosuri.stores.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class StockRequest {
	
	private String manufacturer;

	private String mfName;

	private String itemCode;

	private String itemName;

	private String supplierName;

	private String rack;

	private String batch;

	private Date expiryDate;

	private Double balQuantity;

	private Double balPackQuantity;

	private Double balLooseQuantity;

	private String total;

	private Double mrpPack;

	private Double purRatePerPackAfterGST;

	private Double mrpValue;

	private String itemCategory;

	private String onlineYesNo;

	private String storeId;

	private Double stockValueMrp;

	private Double stockValuePurrate;

	private String updatedBy;
}