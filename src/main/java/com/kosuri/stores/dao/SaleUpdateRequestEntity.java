package com.kosuri.stores.dao;

import java.util.Date;

import lombok.Data;

@Data
public class SaleUpdateRequestEntity {
	
	private String docNumberLineId;

	private String doc_Number;

	private String readableDocNo;

	private Date date;

	private Date time;

	private String custCode;

	private String custName;

	private String patientName;

	private String createdUser;

	private String itemCode;

	private String itemName;

	private String batchNo;

	private Date expiryDate;

	private String mfacCode;

	private String mfacName;

	private String catCode;

	private String catName;

	private String brandName;

	private String packing;

	private Double qtyBox;

	private Double qty;

	private int schQty;

	private Double schDisc;

	private Double saleRate;

	private Double mRP;

	private Double saleValue;

	private Double discPerct;

	private Double discValue;

	private Double taxableAmt;

	private Integer cGSTPer;

	private Integer sGSTPer;

	private Double cGSTAmt;

	private Double sGSTAmt;

	private Integer iGSTPer;

	private Double iGSTAmt;

	private String suppCode;

	private String suppName;

	private Double total;

	private Integer cessPer;

	private Double cessAmt;

	private Integer addCessPer;

	private Double addCessAmt;

	private Double roundOff;

	private String suppBillNo;

	private String professional;

	private String mobile;

	private String lcCode;

	private Double purRate;

	private Double purRateWithGsT;

	private String storeId;

	private String saleMode;
	private String userId;
	private String userIdStoreId;
	private String userIdStoreIdItemCode;
	
	private Double afterDiscount;
	private Double totalPurchasePrice;
	private Double profitOrLoss;
}
