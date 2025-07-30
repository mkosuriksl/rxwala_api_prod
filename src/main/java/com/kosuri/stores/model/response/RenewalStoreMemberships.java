package com.kosuri.stores.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenewalStoreMemberships {

	private String planId;

	private String storeId;

	private String noOfDays;

	private String orderId;

	private Boolean paymentStatus;

}
