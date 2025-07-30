package com.kosuri.stores.model.dto;

import com.kosuri.stores.dao.DCPackageHeader;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class DCPackageHeaderDto {
    
    private String packageId;
    private String userIdStoreId;
    private String packageName;
    private double totalAmount;
    private String updatedBy;
    private LocalDateTime updatedDate;
	private String userId;
	private String storeId;

    // Constructor that maps DCPackageHeader to DCPackageHeaderDto
    public DCPackageHeaderDto(DCPackageHeader packageHeader) {
        this.packageId = packageHeader.getPackageId();
        this.userIdStoreId = packageHeader.getUserIdStoreId();
        this.packageName = packageHeader.getPackageName();
        this.totalAmount = packageHeader.getTotalAmount();
        this.updatedBy = packageHeader.getUpdatedBy();
        this.updatedDate = packageHeader.getUpdatedDate();
        this.userId=packageHeader.getUserId();
        this.storeId=packageHeader.getStoreId();
    }

    // Getters and setters

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getUserIdStoreId() {
        return userIdStoreId;
    }

    public void setUserIdStoreId(String userIdStoreId) {
        this.userIdStoreId = userIdStoreId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
    
    
}
