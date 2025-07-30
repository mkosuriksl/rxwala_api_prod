package com.kosuri.stores.model.search;

import java.util.Date;

import lombok.Data;

@Data
public class StockAndDiscountEnResult {
	private String shopName;
	private String shopLocation;
	private String medicineName;
	private String mfName;
	private Double mrp;
	private String batchNo;
	private Date expiryDate;
	private String ownerContact;
	private String ownerEmail;
	private String storeCategory;
	private String storeBusinessType;
	private String userId;
	private String storeId;
	private String userIdStoreId;
	private int discount;
	public StockAndDiscountEnResult(String shopName, String shopLocation, String medicineName,String mfName, Double mrp, String batchNo,
			Date expiryDate, String ownerContact, String ownerEmail,String storeCategory,String storeBusinessType,String userId,String storeId, String userIdStoreId,int discount) {
		super();
		this.shopName = shopName;
		this.shopLocation = shopLocation;
		this.medicineName = medicineName;
		this.mfName=mfName;
		this.mrp = mrp;
		this.batchNo = batchNo;
		this.expiryDate = expiryDate;
		this.ownerContact = ownerContact;
		this.ownerEmail = ownerEmail;
		this.storeCategory=storeCategory;
		this.storeBusinessType=storeBusinessType;
		this.userId=userId;
		this.storeId=storeId;
		this.userIdStoreId=userIdStoreId;
		this.discount = discount;
	}
	public StockAndDiscountEnResult() {}
	
	
}
