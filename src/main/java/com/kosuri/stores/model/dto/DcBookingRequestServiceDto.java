package com.kosuri.stores.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DcBookingRequestServiceDto {
	private String serviceId;
    private double price;
//    private Double discount;
    private String serviceName;
    private String serviceRequestLineId;
	public DcBookingRequestServiceDto(String serviceId,double price, 
//			Double discount,
			String serviceName, 
			String serviceRequestLineId) {
		super();
		this.serviceId=serviceId;		
		this.price = price;
//		this.discount = discount;
		this.serviceName=serviceName;
		this.serviceRequestLineId = serviceRequestLineId;
	}
	public DcBookingRequestServiceDto() {
	} 
}