package com.kosuri.stores.dao;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Table(name = "stock_pharma_detail")
@Entity
@IdClass(StockId.class)
public class StockEntity {

	@Column(name = "MF")
//    @Id
	private String manufacturer;

	@Column(name = "Mf_Name")
	@Id
	private String mfName;

	@Column(name = "Item_Code")
//    @Id
	private String itemCode;

	@Column(name = "Item_Name")
	@Id
	private String itemName;

	@Column(name = "Supplier_Name")
//    @Id
	private String supplierName;

	@Column(name = "Rack")
	private String rack;

	@Column(name = "Batch")
	@Id
	private String batch;

	@Column(name = "Expiry_Date")
	private Date expiryDate;

	@Column(name = "Bal_Qty")
	private Double balQuantity;

	@Column(name = "Bal_Pack_Qty")
	private Double balPackQuantity;

	@Column(name = "Bal_Loose_Qty")
	private Double balLooseQuantity;
	
	@Column(name = "static_Pack_Qty")
	private Double staticPackQuantity;

	@Column(name = "Total")
	private String total;

	@Column(name = "Mrp_Pack")
	private Double mrpPack;

	@Column(name = "Pur_Rate_Pack_After_GST")
	private Double purRatePerPackAfterGST;

	@Column(name = "Mrp_Value")
	private Double mrpValue;

	@Column(name = "Item_Category")
	private String itemCategory;

	@Column(name = "Online_Yes_No")
	private String onlineYesNo;

	@Column(name = "Store_ID")
	@Id
	private String storeId;

	@Column(name = "Stock_Value_Mrp")
	private Double stockValueMrp;

	@Column(name = "Stock_Value_Purrate")
	private Double stockValuePurrate;

	@Column(name = "Updated_By")
	private String updatedBy;

	@Column(name = "Updated_Date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime updatedAt;

	
	private @Column(name = "User_ID") String userId;
	private @Column(name = "UserIdStoreId_ItemCode") String userIdStoreIdItemCode;
	private @Column(name = "UserIdStoreId") String userIdStoreId;
	@PrePersist
	private void prePersist() {
		this.userIdStoreIdItemCode = userId + "_" + storeId + "_" + itemCode;
	}

	public Double getStaticPackQuantity() {
		return staticPackQuantity;
	}

	public void setStaticPackQuantity(Double staticPackQuantity) {
		this.staticPackQuantity = staticPackQuantity;
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

	public String getUserIdStoreIdItemCode() {
		return userIdStoreIdItemCode;
	}

	public void setUserIdStoreIdItemCode(String userIdStoreIdItemCode) {
		this.userIdStoreIdItemCode = userIdStoreIdItemCode;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getMfName() {
		return mfName;
	}

	public void setMfName(String mfName) {
		this.mfName = mfName;
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

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getRack() {
		return rack;
	}

	public void setRack(String rack) {
		this.rack = rack;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Double getBalQuantity() {
		return balQuantity;
	}

	public void setBalQuantity(Double balQuantity) {
		this.balQuantity = balQuantity;
	}

	public Double getBalPackQuantity() {
		return balPackQuantity;
	}

	public void setBalPackQuantity(Double balPackQuantity) {
		this.balPackQuantity = balPackQuantity;
	}

	public Double getBalLooseQuantity() {
		return balLooseQuantity;
	}

	public void setBalLooseQuantity(Double balLooseQuantity) {
		this.balLooseQuantity = balLooseQuantity;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public Double getMrpPack() {
		return mrpPack;
	}

	public void setMrpPack(Double mrpPack) {
		this.mrpPack = mrpPack;
	}

	public Double getMrpValue() {
		return mrpValue;
	}

	public void setMrpValue(Double mrpValue) {
		this.mrpValue = mrpValue;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	public String getOnlineYesNo() {
		return onlineYesNo;
	}

	public void setOnlineYesNo(String onlineYesNo) {
		this.onlineYesNo = onlineYesNo;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public Double getStockValueMrp() {
		return stockValueMrp;
	}

	public void setStockValueMrp(Double stockValueMrp) {
		this.stockValueMrp = stockValueMrp;
	}

	public Double getStockValuePurrate() {
		return stockValuePurrate;
	}

	public void setStockValuePurrate(Double stockValuePurrate) {
		this.stockValuePurrate = stockValuePurrate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Double getPurRatePerPackAfterGST() {
		return purRatePerPackAfterGST;
	}

	public void setPurRatePerPackAfterGST(Double purRatePerPackAfterGST) {
		this.purRatePerPackAfterGST = purRatePerPackAfterGST;
	}
}
