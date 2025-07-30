package com.kosuri.stores.model.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockReportDTOEn {

	public StockReportDTOEn(String manufacturer, String mfName, String itemCode, String itemName, String supplierName,
			String rack, String batch, Date expiryDate, Double balQuantity, Double balPackQuantity,
			Double balLooseQuantity, String total, Double mrpPack, Double purRatePerPackAfterGST, Double mrpValue,
			String itemCategory, String onlineYesNo, String storeId, Double stockValueMrp, Double stockValuePurrate,
			String updatedBy, LocalDateTime updatedAt, String userId, String userIdStoreIdItemCode,
			String userIdStoreId, Double igstCode, Double minOrderQty, Double offerQty, Double discount) {
		super();
		this.manufacturer = manufacturer;
		this.mfName = mfName;
		this.itemCode = itemCode;
		this.itemName = itemName;
		this.supplierName = supplierName;
		this.rack = rack;
		this.batch = batch;
		this.expiryDate = expiryDate;
		this.balQuantity = balQuantity;
		this.balPackQuantity = balPackQuantity;
		this.balLooseQuantity = balLooseQuantity;
		this.total = total;
		this.mrpPack = mrpPack;
		this.purRatePerPackAfterGST = purRatePerPackAfterGST;
		this.mrpValue = mrpValue;
		this.itemCategory = itemCategory;
		this.onlineYesNo = onlineYesNo;
		this.storeId = storeId;
		this.stockValueMrp = stockValueMrp;
		this.stockValuePurrate = stockValuePurrate;
		this.updatedBy = updatedBy;
		this.updatedAt = updatedAt;
		this.userId = userId;
		this.userIdStoreIdItemCode = userIdStoreIdItemCode;
		this.userIdStoreId = userIdStoreId;
		this.igstCode = igstCode;
		this.minOrderQty = minOrderQty;
		this.offerQty = offerQty;
		this.discount = discount;
	}

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

	private LocalDateTime updatedAt;

	private String userId;
	private String userIdStoreIdItemCode;
	private String userIdStoreId;
	private Double igstCode;
	private Double minOrderQty;
	private Double offerQty;
	private Double discount;
}
