package com.kosuri.stores.model.request;

import java.util.Date;

import lombok.Data;

@Data
public class SalePharmaRequest {

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
	private Double mrp;
	private Double saleValue;
	private Double discPerct;
	private Double discValue;
	private Double taxableAmt;
	private Integer cgstPer;
	private Integer sgstPer;
	private Double cgstAmt;
	private Double sgstAmt;
	private Integer igstPer;
	private Double igstAmt;
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

}
