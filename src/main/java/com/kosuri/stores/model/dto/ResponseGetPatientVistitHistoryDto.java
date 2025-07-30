package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.CustomerBasicInfoDto;
import com.kosuri.stores.dao.CustomerRegistrationInfoDto;
import com.kosuri.stores.dao.PatientDiagnostic;
import com.kosuri.stores.dao.PatientPharmacy;
import com.kosuri.stores.dao.PatientVisitHistoryEntity;
import com.kosuri.stores.dao.StoreBasicInfoDto;

import lombok.Data;

@Data
public class ResponseGetPatientVistitHistoryDto {
	private String message;
	private boolean status;
	private List<PatientVisitHistoryEntity> patientVisitHistoryEntities;
	private List<CustomerBasicInfoDto> customerEntities;
	private List<PatientPharmacy>patientPharmacies;
	private List<StoreBasicInfoDto> pharmacyStoreId;

	private List<PatientDiagnostic>patientDiagnostics;
	
	private List<StoreBasicInfoDto>diagnosticStoreId;

	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
