package com.kosuri.stores.model.dto;

import com.kosuri.stores.model.enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DcBookingRequestServiceStoreDto {
	private String serviceId;
    private double price;
//    private Double discount;
    private Status status;
    private String serviceName;
    
    private String serviceRequestLineId;
	public DcBookingRequestServiceStoreDto(String serviceId,double price, 
//			Double discount,
			Status status,
			String serviceName, 
			String serviceRequestLineId) {
		super();
		this.serviceId=serviceId;		
		this.price = price;
//		this.discount = discount;
		this.status=status;
		this.serviceName=serviceName;
		this.serviceRequestLineId = serviceRequestLineId;
	}
	public DcBookingRequestServiceStoreDto() {
	} 
}