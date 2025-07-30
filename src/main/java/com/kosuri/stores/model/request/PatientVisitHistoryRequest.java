package com.kosuri.stores.model.request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientVisitHistoryRequest {

	private String cid;

	private String visitOrdNo;

	private LocalDate visitingDate;

	private String causeOfVisit;

	private String medication;

	private String treatedBy;

	private String referredTo;

}
