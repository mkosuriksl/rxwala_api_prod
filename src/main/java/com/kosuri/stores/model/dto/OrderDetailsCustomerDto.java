package com.kosuri.stores.model.dto;


import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRetailerOrderHdrEntity;
import com.kosuri.stores.dao.StoreEntity;

import lombok.Data;

@Data
public class OrderDetailsCustomerDto {

	private CustomerRetailerOrderHdrEntity orderHdr;
    private CustomerRegisterEntity customer;
    private StoreEntity store;
	
}