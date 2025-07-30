package com.kosuri.stores.model.dto;

import java.time.LocalDateTime;

import com.kosuri.stores.dao.PrimaryCareEntity;

import lombok.Data;

@Data
public class PrimaryCareDto {

	private String userIdStoreIdServiceId;
	private String serviceId;
	private String serviceName;
	private double price;
	private String description;
	private String userId;
	private String storeId;
	private String serviceCategory;
	private String updatedBy;
	private String status;
	private LocalDateTime amountUpdatedDate;
	private LocalDateTime statusUpdatedDate;
	public PrimaryCareDto(PrimaryCareEntity service) {
		super();
		this.userIdStoreIdServiceId = service.getUserIdStoreIdServiceId();
		this.serviceId = service.getServiceId();
		this.serviceName = service.getServiceName();
		this.price = service.getPrice();
		this.description = service.getDescription();
		this.userId = service.getUserId();
		this.storeId = service.getStoreId();
		this.serviceCategory = service.getServiceCategory();
		this.updatedBy = service.getUpdatedBy();
		this.status = service.getStatus();
		this.amountUpdatedDate = service.getAmountUpdatedDate();
		this.statusUpdatedDate = service.getStatusUpdatedDate();
	}
	
	

}
