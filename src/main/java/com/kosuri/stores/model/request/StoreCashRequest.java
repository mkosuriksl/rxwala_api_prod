package com.kosuri.stores.model.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreCashRequest {

	private LocalDateTime date;

	private Double saleAmount;

	private Double returnAmount;

	private Double onlinePay;

	private Double cashPayment;

	private Double cashHandoverAmount;

	private String handedOverBy;

	private String acceptedBy;

	private Double cashInCounter;

	private String storeId;

}
