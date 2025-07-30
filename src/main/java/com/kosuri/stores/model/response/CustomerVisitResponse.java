package com.kosuri.stores.model.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustomerVisitResponse {

	private String visitOrdNo;

	private LocalDate visitingDate;

	private String causeOfVisit;

	private String medication;

	private String treatedBy;

	private String referredTo;

}
