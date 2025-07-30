package com.kosuri.stores.model.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AmbulanceMasterResponse {

	private String ambulanceRegNo;

	private String userId;

	private String phoneNumber;

	private String baseLocation;

	private String vehicleBrand;

	private String vehicleModel;

	private String rtoRegLocation;

	private String state;

	private String vin;

	private String ownerName;

	private String rtoDoc;

	private String insuDoc;

	private String ambLicDoc;

	private boolean ventilator;

	private String primaryCareNurse;

	private LocalDateTime regDate;

	private String updatedBy;

	private String image;

	private String additionalFeatures;

	private boolean verify;

	private boolean active;

	private String verifiedBy;

	private String updatedby;

}
