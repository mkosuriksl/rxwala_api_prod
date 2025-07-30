package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ambulance_master")
public class AmbulanceMasterEntity {

	@Id
	@Column(name = "Ambulance_Reg_No")
	private String ambulanceRegNo; 

	@Column(name = "UserId")
	private String userId;

	@Column(name = "Phone_Number")
	private String phoneNumber;

	@Column(name = "Base_Location")
	private String baseLocation;

	@Column(name = "Vehicle_Brand")
	private String vehicleBrand;

	@Column(name = "Vehicle_Model")
	private String vehicleModel;

	@Column(name = "Rto_Reg_Location")
	private String rtoRegLocation;

	@Column(name = "State")
	private String state;

	@Column(name = "VIN")
	private String vin;

	@Column(name = "Owner_Name")
	private String ownerName;

	@Column(name = "Rto_Doc",length = 255)
	private String rtoDoc;

	@Column(name = "Insu_Doc",length = 255)
	private String insuDoc;

	@Column(name = "Amb_Lic_Doc",length = 255)
	private String ambLicDoc;

	@Column(name = "Ventilator_")
	private boolean ventilator;

	@Column(name = "Primary_Care_Nurse")
	private String primaryCareNurse;

	@Column(name = "Reg_Date")
	private LocalDateTime regDate;

	@Column(name = "Image")
	private String image;

	@Column(name = "Additional_Features")
	private String additionalFeatures;

	@Column(name = "Verify")
	private boolean verify;

	@Column(name = "Active")
	private boolean active;

	@Column(name = "VerifiedBy")
	private String verifiedBy;

	@Column(name = "Updatedby")
	private String updatedby;

}
