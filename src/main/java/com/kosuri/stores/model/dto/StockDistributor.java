package com.kosuri.stores.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class StockDistributor {
	private String medicineName;
    private String mfName;
    private Double mrp;
    private String batch;
    private Date expiryDate;
    private String userIdStoreIdItemCode;
    private Double discount;
    private Double offerQty;
    private String batchNumber;
    private Double minOrderQty;
    private Integer gst;
    private String itemCategory;
    private String itemSubCategory;
}
