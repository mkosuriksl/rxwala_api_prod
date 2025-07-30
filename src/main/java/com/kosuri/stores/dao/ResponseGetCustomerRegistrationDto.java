package com.kosuri.stores.dao;

import java.util.List;

import lombok.Data;
@Data
public class ResponseGetCustomerRegistrationDto {
	private String message;
	private boolean status;
	private List<CustomerRegistrationInfoDto> customerRegistrationInfo;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
