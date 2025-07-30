package com.kosuri.stores.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAdminStoreMembershipRequest {

	@NotBlank(message = "storeCategory cannot be blank")
	private String storeCategory;

	@NotNull(message = "pricePerUser cannot be blank")
	private Integer pricePerUser;

	@NotBlank(message = "state cannot be blank")
	private String state;

	@NotBlank(message = "district cannot be blank")
	private String district;

	@NotBlank(message = "planId cannot be blank")
	private String planId;

	@NotBlank(message = "noOfDays cannot be blank")
	private String noOfDays;

	@NotBlank(message = "comment cannot be blank")
	private String comment;

	@NotBlank(message = "status cannot be blank")
	private String status;

	private String planIdStorecategory;
}