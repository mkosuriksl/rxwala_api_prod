package com.kosuri.stores.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DistributorRetailerOrderDetailsDto {
//	private long id;
	private String orderlineId;
	private String retailerId;
	private String itemName;
	private String itemCategory;
	private String brandName;
	private String manufacturerName;
	private Integer orderQuantity;
	private Integer deliveryQuantity;
	private Double mrp;
	private Integer discount;
	private String cashDiscount;
	private String offer;
	private Integer sgst;
	private Integer cgst;
	private Double sgstAmount;
	private Double cgstAmount;
	private Double total;
	private String status;
	private String distributorId;
	private String batchNumber;
	private LocalDate expiryDate;
	private String itemCode;
	private String saleType;

}
