package com.kosuri.stores.model.response;

import java.util.List;

import com.kosuri.stores.dao.CustomerRegisterEntity;

import lombok.Data;

@Data
public class CustomerVisitHistories {

	private CustomerRegisterEntity customerDetail;
	private List<CustomerVisitResponse> visitHistories;

}
