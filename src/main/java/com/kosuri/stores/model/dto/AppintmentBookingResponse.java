package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.kosuri.stores.model.enums.Status;

import lombok.Data;

@Data
public class AppintmentBookingResponse {
	private String serviceRequestId;
	private String customerId;
	private String userIdStoreId;
	private Boolean homeService;
	private Boolean walkinService;
	private LocalDate appointmentDate;
	private LocalTime appointmentTime;
	private String updatedBy;
	private LocalDate updatedDate;
	private LocalDate bookingDate;
	private double bookingTotal;
	private Status status;
	public AppintmentBookingResponse(String serviceRequestId, String customerId, String userIdStoreId,
			Boolean homeService, Boolean walkinService, LocalDate appointmentDate, LocalTime appointmentTime,
			String updatedBy, LocalDate updatedDate, LocalDate bookingDate, double bookingTotal, Status status) {
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
		this.bookingTotal = bookingTotal;
		this.status = status;
	}
}
