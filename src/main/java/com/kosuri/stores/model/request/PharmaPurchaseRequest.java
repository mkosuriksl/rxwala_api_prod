package com.kosuri.stores.model.request;

import java.math.BigInteger;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PharmaPurchaseRequest {

	private BigInteger doc_Number;
	private String readableDocNo;
	private Date date;
	private String billNo;
	private Date billDt;
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
	private String dcYear;
	private String dcPrefix;
	private Integer dcSrno;
	private Double qty;
	private Double packQty;
	private Double looseQty;
	private Double schPackQty;
	private Double schLooseQty;
	private Double schDisc;
	private Double saleRate;
	private Double purRate;
	private Double mrp;
	private Double purValue;
	private Double discPer;
	private Double margin;
	private String suppCode;
	private String suppName;
	private Double discValue;
	private Double taxableAmt;
	private Integer gstCode;
	private Integer cgstPer;
	private Integer sgstPer;
	private Double cgstAmt;
	private Double sgstAmt;
	private Double igstPer;
	private Double igstAmt;
	private Double total;
	private Double post;
	private String itemCat;
	private Integer cessPer;
	private Double cessAmt;
	private String storeId;
}
