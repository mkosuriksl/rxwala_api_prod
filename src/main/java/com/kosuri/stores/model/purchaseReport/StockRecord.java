package com.kosuri.stores.model.purchaseReport;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDateTime;
import java.util.Date;

public class StockRecord {
    private String manufacturer;

    private String mfName;

    private String itemCode;

    private String itemName;

    private String supplierName;

    private String rack;

    private String batch;

    private String expiryDate;

    private Double balQuantity;

    private Double balPackQuantity;

    private Double balLooseQuantity;

    private String total;

    private Double mrpPack;

    private Double purRatePerPackAfterGST;

    private Double mrpValue;

    private String itemCategory;

    private String onlineYesNo;

    private String storeId;

    private Double stockValueMrp;

    private Double stockValuePurrate;

    private String updatedBy;

    private String updatedAt;

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

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
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

    public Double getPurRatePerPackAfterGST() {
        return purRatePerPackAfterGST;
    }

    public void setPurRatePerPackAfterGST(Double purRatePerPackAfterGST) {
        this.purRatePerPackAfterGST = purRatePerPackAfterGST;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
