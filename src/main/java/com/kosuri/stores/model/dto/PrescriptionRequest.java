package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class PrescriptionRequest {
	private String visitOrdNo;
	private String storeId;
	private List<PrescriptionHistoryRequest> historyRequests;
}
