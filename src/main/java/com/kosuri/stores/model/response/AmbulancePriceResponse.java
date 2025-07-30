package com.kosuri.stores.model.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AmbulancePriceResponse {

	private String priceId;

	private String ambulanceRegNo;

	private Double pricePerKm;

	private String updateSrcLocation;

	private LocalDateTime updateDate;

	private Double waitingCharges;

	private String driverLicNo;

	private String driverName;

	private boolean active;

	private LocalDateTime createdOn;

	private String createdBy;

	private LocalDateTime updatedOn;

	private String updatedBy;

	private String contactNumber;

}
