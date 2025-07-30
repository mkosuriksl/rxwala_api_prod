package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageResponseDto {
	private String userIdStoreId;
	private String packageId;
	private String packageName;
	private double totalAmount;
	private List<ServiceResponseDto> selectedServices;
}
