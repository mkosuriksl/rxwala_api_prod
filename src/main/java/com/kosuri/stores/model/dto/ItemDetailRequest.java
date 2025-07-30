package com.kosuri.stores.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ItemDetailRequest {
	private String itemCode;
	private String itemName;
	private String batchNo;
	private Date expiryDate;
	private String catCode;
	private String catName;
	private String mfacCode;
	private String mfacName;
	private String brandName;
	private String packing;
	private Double qtyOrBox;
	private Double packQty;
	private Double looseQty;
	private Double schPackQty;
	private Double schLooseQty;
	private Double schDisc;
	private Double purRate;
	private Double mrp;
	private Double purValue;
	private Double discPer;
	private Double discValue;
	private Double taxableAmount;
	private Integer gstCode;
	private Integer cgstPer;
	private Double cgstAmount;
	private Integer sgstPer;
	private Double sgstAmount;
	private Double igstPer;
	private Double igstAmount;
	private Integer cessPer;
	private Double cessAmount;
	private Double total;
	private Double discount;
	private Double afterDiscount;
	private Double totalPurchasePrice;

}
