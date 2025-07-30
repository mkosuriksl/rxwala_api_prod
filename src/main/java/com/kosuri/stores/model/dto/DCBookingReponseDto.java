package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.kosuri.stores.model.enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCBookingReponseDto {
	private String serviceRequestId;
	private double totalAmount;
	private String updatedBy;
	private String userIdStoreId;
	private String customerId;
	private String homeService;
	private String walkinService;
	private LocalDate appointmentDate;
	private LocalTime appointmentTime;
	private LocalDate updatedDate;
	private LocalDate bookingDate;
//	private String status;
	private Status status;
	private List<DcBookingRequestServiceDto> selectedServices;
	public DCBookingReponseDto(String serviceRequestId,double totalAmount,
			String updatedBy, String userIdStoreId, String customerId,String homeService,String walkinService,LocalDate appointmentDate,
			LocalTime appointmentTime,LocalDate updatedDate,LocalDate bookingDate,Status status,List<DcBookingRequestServiceDto> selectedServices) {
		super();
		this.serviceRequestId = serviceRequestId;
		this.totalAmount = totalAmount;
		this.updatedBy = updatedBy;
		this.userIdStoreId = userIdStoreId;
		this.customerId = customerId;
		this.homeService=homeService;
		this.walkinService=walkinService;
		this.appointmentDate=appointmentDate;
		this.appointmentTime=appointmentTime;
		this.updatedDate=updatedDate;
		this.bookingDate=bookingDate;
		this.status=status;
		this.selectedServices = selectedServices;
	}	
}