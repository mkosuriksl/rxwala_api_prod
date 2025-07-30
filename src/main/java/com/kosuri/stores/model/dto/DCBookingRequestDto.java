package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.kosuri.stores.model.enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCBookingRequestDto {
	private String serviceRequestId;
	private double totalAmount;
	private String userIdStoreId;
	private String customerId;
	private String homeService;
	private String walkinService;
	private LocalDate appointmentDate;
	private LocalTime appointmentTime;
//	private String status;
    private Status status;
	private List<DcBookingRequestServiceDto> selectedServices;
	public DCBookingRequestDto(String serviceRequestId,double totalAmount, String userIdStoreId, String customerId,String homeService,String walkinService,LocalDate appointmentDate,
			LocalTime appointmentTime,Status status,List<DcBookingRequestServiceDto> selectedServices) {
		super();
		this.serviceRequestId = serviceRequestId;
		this.totalAmount = totalAmount;
		this.userIdStoreId = userIdStoreId;
		this.customerId = customerId;
		this.homeService=homeService;
		this.walkinService=walkinService;
		this.appointmentDate=appointmentDate;
		this.appointmentTime=appointmentTime;
		this.status=status;
		this.selectedServices = selectedServices;

	}	
}