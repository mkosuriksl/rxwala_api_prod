package com.kosuri.stores.dao;

import java.time.LocalDate;

import com.kosuri.stores.model.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "add_ambulance")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddAmbulance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AMBULANCE_SEQ_ID")
	private Long ambulanceSeqId;

	@Column(name = "AMBULANCE_REG_NO")
	private String ambulanceRegNo;

	@Column(name = "AMBULANCE_BRAND")
	private String ambulanceBrand;

	@Column(name = "AMBULANCE_MODEL")
	private String ambulanceModel;

	@Column(name = "MODEL_YEAR")
	private String modelYear;

	@Column(name = "CURRENT_MILEAGE")
	private String currentMileage;

	@Column(name = "FUEL_TYPE")
	private String fuelType;

	@Column(name = "BODY_TYPE")
	private String bodyType;

	@Column(name = "TRANSMISSION")
	private String transmission;

	@Column(name = "KM_DRIVEN")
	private String kmDriven;

	@Column(name = "NO_OF_PASSENGER")
	private String numberOfPassenger;

	@Column(name = "COLOR")
	private String color;

	@Column(name = "INSURENCE_COMP_NAME")
	private String insuranceCompanyName;

	@Column(name = "CERTIFIED_COMP_NAME")
	private String certifiedCompanyName;

	@Column(name = "REGISTERED_YEAR")
	private String registeredYear;

	@Column(name = "REGISTERED_CITY")
	private String registeredCity;

	@Column(name = "REGISTERED_STATE")
	private String registeredState;

	@Column(name = "RC_DOC")
	private String rcDoc;

	@Column(name = "INSURANCE_DOC")
	private String insuranceDoc;

	@Column(name = "AMBULANCE_PHOTO")
	private String ambulancePhoto;

	@Column(name = "AMBULANCE_OWNER_ID")
	private String ambulanceOwnerId;

	@Column(name = "AMBULANCE_GEN_ID")
	private String ambulanceGenId;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private Status status;

	@Column(name = "MOBILE_NO")
	private String mobileNo;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;

	@Column(name = "DISTRICT")
	private String district;

	@Column(name = "PINCODE")
	private String pincode;

	@Column(name = "AMBULANCE_ADDED_DATE")
	private LocalDate ambulanceAddedDate;

	@Column(name = "AMBULANCE_PLATE_STATUS")
	private String ambulancePlateStatus;

	@Column(name = "AMBULANCE_EXPIRY_DATE")
	private String ambulanceExpiryDate;

	@Column(name = "AMBULANCE_VALID_DAYS")
	private String ambulanceValidDays;
	
}
