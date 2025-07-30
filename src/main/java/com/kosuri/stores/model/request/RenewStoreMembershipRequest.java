package com.kosuri.stores.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RenewStoreMembershipRequest {

	@NotBlank(message = "storeId cannot be blank")
	private String storeId;

	@NotBlank(message = "planId cannot be blank")
	private String planId;

	@NotNull(message = "noOfDays cannot be blank")
	private String noOfDays;

	@NotNull(message = "amount cannot be blank")
	private Integer orderAmount;

	@NotNull(message = "amount cannot be blank")
	private Integer amount;

	@NotBlank(message = "paymentMethod cannot be blank")
	private String paymentMethod;

	@NotBlank(message = "userId cannot be blank")
	private String userId;
	
	@NotBlank(message = "userIdstoreId cannot be blank")
	private String userIdstoreId;
	
}
