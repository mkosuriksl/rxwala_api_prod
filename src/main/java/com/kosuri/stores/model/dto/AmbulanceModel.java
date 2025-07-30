package com.kosuri.stores.model.dto;

import java.time.LocalDate;

import com.kosuri.stores.model.enums.Status;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AmbulanceModel {

	private Long ambulanceSeqId;

	private String ambulanceRegNo;

	private String ambulanceBrand;

	private String ambulanceModel;

	private String modelYear;

	private String currentMileage;

	private String fuelType;

	private String bodyType;

	private String transmission;

	private String kmDriven;

	private String numberOfPassenger;

	private String color;

	private String insuranceCompanyName;

	private String certifiedCompanyName;

	private String registeredYear;

	private String registeredCity;

	private String registeredState;

	private String rcDoc;

	private String insuranceDoc;

	private String ambulancePhoto;

	private String ambulanceOwnerId;

	private String ambulanceGenId;

	private Status status;

	private String mobileNo;

	private String address;

	private String city;

	private String state;

	private String district;

	private String pincode;

	private LocalDate ambulanceAddedDate;

	private String ambulancePlateStatus;

	private String ambulanceExpiryDate;

	private int ambulanceValidDays;
	
    private String memberShipPlanId;
	
}
