package com.kosuri.stores.model.dto;

import java.math.BigInteger;
import java.util.Date;

import lombok.Data;

@Data
public class PurchaseUpdateStatus {
	private Integer id;
	private  BigInteger doc_Number;
	private  String readableDocNo;
	private  Date date;
	private  Date billDt;
	private  String itemCode;
	private  String itemName;
	private  String batchNo;
	private  Date expiryDate;
	private  String catCode;
	private  String catName;
	private  String mfacCode;
	private  String mfacName;
	private  String brandName;
	private  String packing;
	private  String dcYear;
	private  String dcPrefix;
	private  Integer dcSrno;
	private  Double qty;
	private  Double packQty;
	private  Double looseQty;
	private  Double schPackQty;
	private  Double schLooseQty;
	private  Double schDisc;
	private  Double SaleRate;
	private  Double purRate;
	private  Double mRP;
	private  Double purValue;
	private  Double discPer;
	private  Double margin;
	private  String suppCode;
	private  String suppName;
	private  Double discValue;
	private  Double taxableAmt;
	private  Integer gstCode;
	private  Integer cGSTPer;
	private  Integer sGSTPer;
	private  Double cGSTAmt;
	private  Double sGSTAmt;
	private  Double iGSTPer;
	private  Double iGSTAmt;
	private  Double total;
	private  Double post;
	private  String itemCat;
	private  Integer cessPer;
	private  Double cessAmt;
	private  String storeId;
	private  String userId;
	private  String userIdStoreIdItemCode;
	private  String userIdStoreId;
	private  Double discount;
	private  Double afterDiscount;
	private  Double totalPurchasePrice;
}