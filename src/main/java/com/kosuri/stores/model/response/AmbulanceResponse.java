package com.kosuri.stores.model.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AmbulanceResponse {

	private String ambulanceRegNo;

	private boolean active;

	private LocalDateTime createdOn;

	private String createdBy;

	private LocalDateTime updatedOn;

	private String updatedBy;

	private String additionalFeatures;

	private String baseLocation;

	private String ownerName;

	private String phoneNumber;

	private LocalDateTime regDate;

	private String rtoDoc;

	private String state;

	private String userId;

	private String vehicleBrand;

	private String vehicleModel;

	private boolean verified;

	private String verifiedBy;

	private String vin;

	private String rtoRegLocation;

}
