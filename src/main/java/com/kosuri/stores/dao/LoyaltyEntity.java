package com.kosuri.stores.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "loyalty_pharma")
public class LoyaltyEntity {

	@Id
	@Column(name = "Store_ID")
	String storeId;

	@Column(name = "Store_Category")
	String storeCategory;

	@Column(name = "Loyalty_Points")
	Integer loyaltyPoints;

	@Column(name = "Sales_Volume")
	Double salesVolume;

	@Column(name = "Fixed_Discount_Per100")
	Double fixedDiscountPercentage;

	@Column(name = "Total_Sales_Vol")
	Double totalSalesVolume;

	@Column(name = "Min_Loyalty_points")
	Integer minLoyaltyPoints;

	@Column(name = "Max_Sales_Volume")
	String maxSalesVolume;

	@Column(name = "Loyalty_Category")
	String loyaltyCategory;

	public String getStoreId() {
		return storeId;
	}

	public Integer getMinLoyaltyPoints() {
		return minLoyaltyPoints;
	}

	public void setMinLoyaltyPoints(Integer minLoyaltyPoints) {
		this.minLoyaltyPoints = minLoyaltyPoints;
	}

	public String getMaxSalesVolume() {
		return maxSalesVolume;
	}

	public void setMaxSalesVolume(String maxSalesVolume) {
		this.maxSalesVolume = maxSalesVolume;
	}

	public String getLoyaltyCategory() {
		return loyaltyCategory;
	}

	public void setLoyaltyCategory(String loyaltyCategory) {
		this.loyaltyCategory = loyaltyCategory;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreCategory() {
		return storeCategory;
	}

	public void setStoreCategory(String storeCategory) {
		this.storeCategory = storeCategory;
	}

	public Integer getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(Integer loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
	}

	public Double getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(Double salesVolume) {
		this.salesVolume = salesVolume;
	}

	public Double getFixedDiscountPercentage() {
		return fixedDiscountPercentage;
	}

	public void setFixedDiscountPercentage(Double fixedDiscountPercentage) {
		this.fixedDiscountPercentage = fixedDiscountPercentage;
	}

	public Double getTotalSalesVolume() {
		return totalSalesVolume;
	}

	public void setTotalSalesVolume(Double totalSalesVolume) {
		this.totalSalesVolume = totalSalesVolume;
	}
}
