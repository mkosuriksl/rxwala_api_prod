package com.kosuri.stores.model.dto;

import com.kosuri.stores.model.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
	private String serviceID;
	private Double amount;
	private Double discount;
	private String serviceName;
	private Status status;
	private String packageIdLineId;
}
