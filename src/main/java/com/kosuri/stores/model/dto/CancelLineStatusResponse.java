package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelLineStatusResponse {
	private String serviceRequestId;
	private List<String> canceledLineIds;
	private boolean allCanceled;
	private double bookingTotal;
	private String message;
	public CancelLineStatusResponse(String serviceRequestId, List<String> canceledLineIds, boolean allCanceled,
			double bookingTotal,String message) {
		super();
		this.serviceRequestId = serviceRequestId;
		this.canceledLineIds = canceledLineIds;
		this.allCanceled = allCanceled;
		this.bookingTotal = bookingTotal;
		this.message = message;
	}
	
	
}
