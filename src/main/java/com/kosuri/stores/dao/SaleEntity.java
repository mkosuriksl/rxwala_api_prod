package com.kosuri.stores.dao;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "sales_pharma_detail")
public class SaleEntity {
	public String getSaleType() {
		return saleType;
	}

	public void setSaleType(String saleType) {
		this.saleType = saleType;
	}

//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Id
//	private Integer id;

	@Id
	@Column(name = "Doc_No_LineId")
	private String docNumberLineId;
	
	public String getDocNumberLineId() {
		return docNumberLineId;
	}

	public void setDocNumberLineId(String docNumberLineId) {
		this.docNumberLineId = docNumberLineId;
	}

	@Column(name = "Doc_No")
	private String docNumber;
	
	@Column(name = "Read_Doc_No", nullable = true, length = 45)
	private String readableDocNo;
	public Integer getGstCode() {
		return gstCode;
	}

	public void setGstCode(Integer gstCode) {
		this.gstCode = gstCode;
	}

	@Column(name = "Date")
	@Temporal(TemporalType.DATE)
	private Date date;
	@Column(name = "Time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;
	@Column(name = "Cust_Code", nullable = true, length = 45)
	private String custCode;
	@Column(name = "Cust_Name", nullable = true, length = 45)
	private String custName;
	@Column(name = "Patient_Name", nullable = true, length = 45)
	private String patientName;
	@Column(name = "Created_User", nullable = true, length = 45)
	private String createdUser;
	@Column(name = "Item_Code", nullable = true, length = 45)
	private String itemCode;
	@Column(name = "Item_Name", nullable = true, length = 45)
	private String itemName;
	@Column(name = "Batch_No", nullable = true, length = 45)
	private String batchNo;
	@Column(name = "Exp_Dt", nullable = true, length = 45)
	private Date expiryDate;
	@Column(name = "Mfac_Code", nullable = true, length = 45)
	private String mfacCode;
	@Column(name = "Mfac_Name", nullable = true, length = 45)
	private String mfacName;
	@Column(name = "Cat_Code", nullable = true, length = 45)
	private String catCode;
	@Column(name = "Category_Name", nullable = true, length = 45)
	private String catName;
	@Column(name = "Brand_Name", nullable = true, length = 45)
	private String brandName;
	private @Column(name = "Gst_Code") Integer gstCode;
	@Column(name = "Packing")
	private String packing;
    @Column(name = "OrderId")
    private String orderId;
    
    @Column(name = "sale_type")
    private String saleType;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPacking() {
		return packing;
	}

	public void setPacking(String packing) {
		this.packing = packing;
	}

	@Column(name = "qty_in_box", nullable = true, length = 45)
	private Double qtyBox;
	@Column(name = "Qty", nullable = true, length = 45)
	private Double qty;
	@Column(name = "Sch_Qty", nullable = true, length = 45)
	private int schQty;
	@Column(name = "Sch_Disc", nullable = true, length = 45)
	private Double schDisc;
	@Column(name = "Sale_Rate", nullable = true, length = 45)
	private Double saleRate;
	@Column(name = "Mrp", nullable = true, length = 45)
	private Double mRP;
	@Column(name = "Sale_Value", nullable = true, length = 45)
	private Double saleValue;
	@Column(name = "Disc_Perct", nullable = true, length = 45)
	private Double discPerct;
	@Column(name = "Disc_Value", nullable = true, length = 45)
	private Double discValue;
	@Column(name = "Taxable_Amt", nullable = true, length = 45)
	private Double taxableAmt;
	@Column(name = "Cgst_Per", nullable = true, length = 45)
	private Integer cGSTPer;
	@Column(name = "Sgst_Per", nullable = true, length = 45)
	private Integer sGSTPer;
	@Column(name = "Cgst_Amt", nullable = true, length = 45)
	private Double cGSTAmt;
	@Column(name = "Sgst_Amt", nullable = true, length = 45)
	private Double sGSTAmt;
	@Column(name = "Igst_Per", nullable = true, length = 45)
	private Integer iGSTPer;
	@Column(name = "Igst_Amt", nullable = true, length = 45)
	private Double iGSTAmt;
	@Column(name = "Supp_Code", nullable = true, length = 45)
	private String suppCode;
	@Column(name = "Supp_Name", nullable = true, length = 45)
	private String suppName;
	@Column(name = "Total", nullable = true, length = 45)
	private Double total;
	@Column(name = "Cess_Per", nullable = true, length = 45)
	private Integer cessPer;
	@Column(name = "Cess_Amt", nullable = true, length = 45)
	private Double cessAmt;
	@Column(name = "Add_Cess_Per", nullable = true, length = 45)
	private Integer addCessPer;
	@Column(name = "Add_Cess_Amt", nullable = true, length = 45)
	private Double addCessAmt;
	@Column(name = "Round_Off", nullable = true, length = 45)
	private Double roundOff;
	@Column(name = "Supp_Bill_No", nullable = true, length = 45)
	private String suppBillNo;

	@Column(name = "Professional", nullable = true, length = 45)
	private String professional;
	@Column(name = "Mobile", nullable = true, length = 45)
	private String mobile;
	@Column(name = "Lc_Code", nullable = true, length = 45)
	private String lcCode;
	@Column(name = "Pur_Rate", nullable = true, length = 45)
	private Double purRate;
	@Column(name = "Pur_Rate_With_GSt", nullable = true, length = 45)
	private Double purRateWithGsT;
	@Column(name = "Store_ID", nullable = true, length = 45)
	private String storeId;
	@Column(name = "Sale_Mode", nullable = true, length = 45)
	private String saleMode;
	private @Column(name = "User_ID") String userId;
	public Double getAfterDiscount() {
		return afterDiscount;
	}

	public void setAfterDiscount(Double afterDiscount) {
		this.afterDiscount = afterDiscount;
	}

	public Double getTotalPurchasePrice() {
		return totalPurchasePrice;
	}

	public void setTotalPurchasePrice(Double totalPurchasePrice) {
		this.totalPurchasePrice = totalPurchasePrice;
	}

	public Double getProfitOrLoss() {
		return profitOrLoss;
	}

	public void setProfitOrLoss(Double profitOrLoss) {
		this.profitOrLoss = profitOrLoss;
	}

	private @Column(name = "UserIdStoreId_ItemCode") String userIdStoreIdItemCode;
	private @Column(name = "After_Discount") Double afterDiscount;
	private @Column(name = "Total_Purchase_Price") Double totalPurchasePrice;
	private @Column(name = "profitOrLoss") Double profitOrLoss;
	@PrePersist
	private void prePersist() {
		this.userIdStoreIdItemCode = userId + "_" + storeId + "_" + itemCode;
	}
	private @Column(name = "UserIdStoreId") String userIdStoreId;

	public String getUserIdStoreIdItemCode() {
		return userIdStoreIdItemCode;
	}

	public void setUserIdStoreIdItemCode(String userIdStoreIdItemCode) {
		this.userIdStoreIdItemCode = userIdStoreIdItemCode;
	}

	public String getUserIdStoreId() {
		return userIdStoreId;
	}

	public void setUserIdStoreId(String userIdStoreId) {
		this.userIdStoreId = userIdStoreId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getReadableDocNo() {
		return readableDocNo;
	}

	public void setReadableDocNo(String readableDocNo) {
		this.readableDocNo = readableDocNo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getMfacCode() {
		return mfacCode;
	}

	public void setMfacCode(String mfacCode) {
		this.mfacCode = mfacCode;
	}

	public String getMfacName() {
		return mfacName;
	}

	public void setMfacName(String mfacName) {
		this.mfacName = mfacName;
	}

	public String getCatCode() {
		return catCode;
	}

	public void setCatCode(String catCode) {
		this.catCode = catCode;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Double getQtyBox() {
		return qtyBox;
	}

	public void setQtyBox(Double qtyBox) {
		this.qtyBox = qtyBox;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public int getSchQty() {
		return schQty;
	}

	public void setSchQty(int schQty) {
		this.schQty = schQty;
	}

	public Double getSchDisc() {
		return schDisc;
	}

	public void setSchDisc(Double schDisc) {
		this.schDisc = schDisc;
	}

	public Double getSaleRate() {
		return saleRate;
	}

	public void setSaleRate(Double saleRate) {
		this.saleRate = saleRate;
	}

	public Double getmRP() {
		return mRP;
	}

	public void setmRP(Double mRP) {
		this.mRP = mRP;
	}

	public Double getSaleValue() {
		return saleValue;
	}

	public void setSaleValue(Double saleValue) {
		this.saleValue = saleValue;
	}

	public Double getDiscPerct() {
		return discPerct;
	}

	public void setDiscPerct(Double discPerct) {
		this.discPerct = discPerct;
	}

	public Double getDiscValue() {
		return discValue;
	}

	public void setDiscValue(Double discValue) {
		this.discValue = discValue;
	}

	public Double getTaxableAmt() {
		return taxableAmt;
	}

	public void setTaxableAmt(Double taxableAmt) {
		this.taxableAmt = taxableAmt;
	}

	public Integer getcGSTPer() {
		return cGSTPer;
	}

	public void setcGSTPer(Integer cGSTPer) {
		this.cGSTPer = cGSTPer;
	}

	public Integer getsGSTPer() {
		return sGSTPer;
	}

	public void setsGSTPer(Integer sGSTPer) {
		this.sGSTPer = sGSTPer;
	}

	public Double getcGSTAmt() {
		return cGSTAmt;
	}

	public void setcGSTAmt(Double cGSTAmt) {
		this.cGSTAmt = cGSTAmt;
	}

	public Double getsGSTAmt() {
		return sGSTAmt;
	}

	public void setsGSTAmt(Double sGSTAmt) {
		this.sGSTAmt = sGSTAmt;
	}

	public Integer getiGSTPer() {
		return iGSTPer;
	}

	public void setiGSTPer(Integer iGSTPer) {
		this.iGSTPer = iGSTPer;
	}

	public Double getiGSTAmt() {
		return iGSTAmt;
	}

	public void setiGSTAmt(Double iGSTAmt) {
		this.iGSTAmt = iGSTAmt;
	}

	public String getSuppCode() {
		return suppCode;
	}

	public void setSuppCode(String suppCode) {
		this.suppCode = suppCode;
	}

	public String getSuppName() {
		return suppName;
	}

	public void setSuppName(String suppName) {
		this.suppName = suppName;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Integer getCessPer() {
		return cessPer;
	}

	public void setCessPer(Integer cessPer) {
		this.cessPer = cessPer;
	}

	public Double getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(Double cessAmt) {
		this.cessAmt = cessAmt;
	}

	public Integer getAddCessPer() {
		return addCessPer;
	}

	public void setAddCessPer(Integer addCessPer) {
		this.addCessPer = addCessPer;
	}

	public Double getAddCessAmt() {
		return addCessAmt;
	}

	public void setAddCessAmt(Double addCessAmt) {
		this.addCessAmt = addCessAmt;
	}

	public Double getRoundOff() {
		return roundOff;
	}

	public void setRoundOff(Double roundOff) {
		this.roundOff = roundOff;
	}

	public String getSuppBillNo() {
		return suppBillNo;
	}

	public void setSuppBillNo(String suppBillNo) {
		this.suppBillNo = suppBillNo;
	}

	public String getProfessional() {
		return professional;
	}

	public void setProfessional(String professional) {
		this.professional = professional;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLcCode() {
		return lcCode;
	}

	public void setLcCode(String lcCode) {
		this.lcCode = lcCode;
	}

	public Double getPurRate() {
		return purRate;
	}

	public void setPurRate(Double purRate) {
		this.purRate = purRate;
	}

	public Double getPurRateWithGsT() {
		return purRateWithGsT;
	}

	public void setPurRateWithGsT(Double purRateWithGsT) {
		this.purRateWithGsT = purRateWithGsT;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getSaleMode() {
		return saleMode;
	}

	public void setSaleMode(String saleMode) {
		this.saleMode = saleMode;
	}
}
