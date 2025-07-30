package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.kosuri.stores.model.enums.Status;

import lombok.Data;

@Data
public class AppintmentBookingPCUpdateResponse {
	private String serviceRequestId;
	private String customerId;
	private String userIdStoreId;
	private String homeService;
	private String walkinService;
	private LocalDate appointmentDate;
	private LocalTime appointmentTime;
	private String updatedBy;
	private LocalDate updatedDate;
	private LocalDate bookingDate;
	private double totalAmount;
	private Status status;
	public AppintmentBookingPCUpdateResponse(String serviceRequestId, String customerId, String userIdStoreId,
			String homeService, String walkinService, LocalDate appointmentDate, LocalTime appointmentTime,
			String updatedBy, LocalDate updatedDate, LocalDate bookingDate, double totalAmount, Status status) {
		super();
		this.serviceRequestId = serviceRequestId;
		this.customerId = customerId;
		this.userIdStoreId = userIdStoreId;
		this.homeService = homeService;
		this.walkinService = walkinService;
		this.appointmentDate = appointmentDate;
		this.appointmentTime = appointmentTime;
		this.updatedBy = updatedBy;
		this.updatedDate = updatedDate;
		this.bookingDate = bookingDate;
		this.totalAmount = totalAmount;
		this.status = status;
	}
	
	
}
