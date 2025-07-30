package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "amb_booking_details")
public class AmbulanceBookingDetailEntity {

	@Id
	@Column(name = "Booking_No")
	private String bookingNo; //prefix amb+timestamp

	@Column(name = "Booking_Date")
	private LocalDateTime bookingDate;

	@Column(name = "Patient_Name")
	private String patientName;

	@Column(name = "From_Location")
	private String fromLocation;

	@Column(name = "To_Location")
	private String toLocation;

	@Column(name = "Customer_Cont_Num")
	private String customerContNum;

	@Column(name = "Contact_Person")
	private String contactPerson;

	@Column(name = "Booked_By")
	private String bookedBy;

	@Column(name = "Status")
	private String status;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "active")
	private boolean active;

	@Column(name = "created_on")
	private LocalDateTime createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_on")
	private LocalDateTime updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "ambulance_reg_no")
	private String ambulanceRegNo;

}
