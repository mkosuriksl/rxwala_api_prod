package com.kosuri.stores.model.search;

import java.util.Date;

import lombok.Data;

@Data
public class StockAndDiscountResult {
	private String shopName;
	private String shopLocation;
	private String medicineName;
	private Double mrp;
	private String batchNo;
	private Date expiryDate;
	private String ownerContact;
	private String ownerEmail;
	private int discount;
	private String manufacturer;
	public StockAndDiscountResult(String shopName, String shopLocation, String medicineName, Double mrp, String batchNo,
			Date expiryDate, String ownerContact, String ownerEmail, int discount) {
		super();
		this.shopName = shopName;
		this.shopLocation = shopLocation;
		this.medicineName = medicineName;
		this.mrp = mrp;
		this.batchNo = batchNo;
		this.expiryDate = expiryDate;
		this.ownerContact = ownerContact;
		this.ownerEmail = ownerEmail;
		this.discount = discount;
	}
	public StockAndDiscountResult() {}
	public StockAndDiscountResult(String shopName, String shopLocation, String medicineName, Double mrp, String batchNo,
			Date expiryDate, String ownerContact, String ownerEmail, int discount, String manufacturer) {
		super();
		this.shopName = shopName;
		this.shopLocation = shopLocation;
		this.medicineName = medicineName;
		this.mrp = mrp;
		this.batchNo = batchNo;
		this.expiryDate = expiryDate;
		this.ownerContact = ownerContact;
		this.ownerEmail = ownerEmail;
		this.discount = discount;
		this.manufacturer = manufacturer;
	}
	
	
}
