package com.kosuri.stores.model.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AmbulancePriceRequest {


	@NotBlank(message = "ambulance RegNo is Required")
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
