package com.kosuri.stores.dao;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "customer_loyalty")
@IdClass(CustomerLoyaltyEntityId.class)
public class CustomerLoyaltyEntity {
	@Column(name = "Customer_Name")
	@Id
	private String customerName;

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	@Column(name = "Customer_Phone")
	@Id
	private String customerPhone;

	@Column(name = "Total_Sales")
	private Double totalSales;

	@Column(name = "Loyalty_Points")
	private Integer loyaltyPoints;

	@Column(name = "Discount_Amt")
	private Double discountAmount;

	@Column(name = "Discounted_Date")
	@Temporal(TemporalType.DATE)
	@Id
	private Date discountedDate;

	@Column(name = "Store_ID")
	private String storeId;

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Double getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(Double totalSales) {
		this.totalSales = totalSales;
	}

	public Integer getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(Integer loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
	}

	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public Date getDiscountedDate() {
		return discountedDate;
	}

	public void setDiscountedDate(Date discountedDate) {
		this.discountedDate = discountedDate;
	}

}
