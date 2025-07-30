package com.kosuri.stores.model.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AmbulanceMasterRequest {

	@NotBlank(message = "Ambulance Reg No is Required")
	private String ambulanceRegNo;

	@NotBlank(message = "Phone Number is Required")
	private String phoneNumber;

	@NotBlank(message = "Base Location is Required")
	private String baseLocation;

	@NotBlank(message = "Vehicle Brand is Required")
	private String vehicleBrand;

	@NotBlank(message = "Vehicle Model is Required")
	private String vehicleModel;

	@NotBlank(message = "RTO Reg Location is Required")
	private String rtoRegLocation;

	@NotBlank(message = "State is Required")
	private String state;

	@NotBlank(message = "VIN is Required")
	private String vin;

	@NotBlank(message = "Owner Name is Required")
	private String ownerName;

	@NotBlank(message = "RTO Doc is Required")
	private String rtoDoc;

	@NotBlank(message = "Insurance Document is Required")
	private String insuDoc;

	@NotBlank(message = "Ambulance License Document is Required")
	private String ambLicDoc;

	private boolean ventilator;

	@NotBlank(message = "Primary Care Nurse is Required")
	private String primaryCareNurse;

	 @NotNull(message = "Reg Date is Required")
	private LocalDateTime regDate;

	@NotBlank(message = "Image is Required")
	private String image;

	private String additionalFeatures;

}
