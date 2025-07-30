package com.kosuri.stores.model.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AmbulanceBookingDetailRequest {

	@NotNull(message = "Booking Date is Required")
	private LocalDateTime bookingDate;

	@NotBlank(message = "Patient Name is Required")
	private String patientName;

	@NotBlank(message = "From Location is Required")
	private String fromLocation;

	@NotBlank(message = "To Location is Required")
	private String toLocation;

	@NotBlank(message = "Customer Contact Number is Required")
	private String customerContNum;

	@NotBlank(message = "Contact Person is Required")
	private String contactPerson;

	@NotBlank(message = "Booked By is Required")
	private String bookedBy;

	@NotBlank(message = "Status is Required")
	private String status;

	@NotBlank(message = "Remarks is Required")
	private String remarks;

	@NotBlank(message = "Created By is Required")
	private String createdBy;

	@NotBlank(message = "Updated By is Required")
	private String updatedBy;

	@NotBlank(message = "Ambulance Reg No is Required")
	private String ambulanceRegNo;

}
