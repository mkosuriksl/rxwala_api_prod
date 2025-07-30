package com.kosuri.stores.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAdminStoreMembershipRequest {
	@NotBlank(message = "noOfDays cannot be blank")
	private String noOfDays;

	@NotNull(message = "amount cannot be blank")
	private Integer amount;

	@NotBlank(message = "Please enter planIdStorecategory for updating store_membership.")
	private String planIdStorecategory;

}
