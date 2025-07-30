package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ambulance_price")
public class AmbulancePriceEntity {

	@Id
	@Column(name = "price_id")
	private String priceId;//Comb of ambulanceRegNo+price

	@Column(name = "Ambulance_Reg_No")
	private String ambulanceRegNo;

	@Column(name = "Price_Per_Km")
	private Double pricePerKm;

	@Column(name = "Update_Src_Location")
	private String updateSrcLocation;

	@Column(name = "Update_Date")
	private LocalDateTime updateDate;

	@Column(name = "Waiting_Charges")
	private Double waitingCharges;

	@Column(name = "Driver_Lic_No")
	private String driverLicNo;

	@Column(name = "Driver_Name")
	private String driverName;

	@Column(name = "active")
	private boolean active;

	@Column(name = "created_on")
	private LocalDateTime createdOn;

	@Column(name = "created_by")
	private String createdBy; //UserName

	@Column(name = "updated_on")
	private LocalDateTime updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "contact_number")
	private String contactNumber;

}
