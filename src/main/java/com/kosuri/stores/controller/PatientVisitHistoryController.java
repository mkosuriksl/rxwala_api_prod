package com.kosuri.stores.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.CustomerBasicInfoDto;
import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.PatientDiagnostic;
import com.kosuri.stores.dao.PatientDiagnosticRepository;
import com.kosuri.stores.dao.PatientPharmacy;
import com.kosuri.stores.dao.PatientPharmacyRepository;
import com.kosuri.stores.dao.PatientVisitHistoryEntity;
import com.kosuri.stores.dao.StoreBasicInfoDto;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.handler.PatientVisitHistoryHandler;
import com.kosuri.stores.model.dto.ResponseGetPatientVistitHistoryDto;
import com.kosuri.stores.model.request.PatientVisitHistoryRequest;
import com.kosuri.stores.model.response.GenericResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/patient-visit")
public class PatientVisitHistoryController {

	@Autowired
	private PatientDiagnosticRepository patientDiagnosticRepository;
	@Autowired
	private PatientVisitHistoryHandler patientVisitHistoryHandler;

	@Autowired
	private PatientPharmacyRepository patientPharmacyRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;
//	@GetMapping("/getAll")
//	public ResponseEntity<CustomerVisitHistories> getAllPatientVisitHistoryByCustomerId(
//			@RequestParam String cid) {
//		log.info(">>Controller Logger getAllPatientVisitHistoryByCustomerId({})");
//		return ResponseEntity.status(HttpStatus.OK)
//				.body(patientVisitHistoryHandler.getAllPatientVisitHistoryByCustomerId(cid));
//	}

//	@GetMapping("/{visitNo}")
//	public ResponseEntity<PatientVisitHistoryEntity> getPatientVisitHistoryByCidAndById(@RequestParam String cid,
//			@PathVariable String visitNo) {
//		log.info(">>Controller Logger getStoreCashByStoreIdAndByStoreCashId({})", cid, visitNo);
//		return ResponseEntity.status(HttpStatus.OK)
//				.body(patientVisitHistoryHandler.getPatientVisitHistoryByCidAndById(cid, visitNo));
//	}

	@PostMapping("/add")
	public ResponseEntity<GenericResponse> savePatientVisitHistory(
			@RequestBody PatientVisitHistoryRequest patientVisitHistoryRequest) {
		log.info(">>Controller Logger save saveStoreCash({})", patientVisitHistoryRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(patientVisitHistoryHandler.savePatientVisitHistory(patientVisitHistoryRequest));

	}

	@GetMapping("/get")
	public ResponseEntity<ResponseGetPatientVistitHistoryDto> get(@RequestParam(required = false) String visitOrdNo,
			@RequestParam(required = false) String cId, @RequestParam(required = false) String name,
			@RequestParam(required = false) String email, @RequestParam(required = false) String phoneNumber,
			@RequestParam(required = false) String pharmacyStoreId, // ← new param
			@RequestParam(required = false) String diagnosticStoreId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);

		// Step 1: Fetch visit history with basic filters
		Page<PatientVisitHistoryEntity> initialPage = patientVisitHistoryHandler.get(visitOrdNo, cId, name, email,
				phoneNumber, pharmacyStoreId, diagnosticStoreId, pageable);

		List<PatientVisitHistoryEntity> visitList = initialPage.getContent();

		// Step 2: Get all cIds from visits
		List<String> allCIds = visitList.stream().map(PatientVisitHistoryEntity::getCId).filter(Objects::nonNull)
				.distinct().toList();

		// Step 3: Fetch customers based on cIds
		List<CustomerRegisterEntity> customers = customerRegisterRepository.findByCIdIn(allCIds);

		// Step 4: Filter customers based on name/email/phoneNumber
		List<String> matchedCIds = customers.stream().map(CustomerRegisterEntity::getCId).toList();

		// Step 5: Filter visit records by matched cIds
		List<PatientVisitHistoryEntity> filteredVisits = visitList.stream()
				.filter(v -> matchedCIds.contains(v.getCId())).toList();

		// Optional: Paginate filtered list manually (if you need paging after
		// filtering)
		Page<PatientVisitHistoryEntity> finalPage = new PageImpl<>(filteredVisits, pageable, filteredVisits.size());

		// Step 6: Build response DTO
		ResponseGetPatientVistitHistoryDto response = new ResponseGetPatientVistitHistoryDto();
		response.setMessage("Patient visit history details retrieved successfully.");
		response.setStatus(true);
		response.setPatientVisitHistoryEntities(finalPage.getContent());
		response.setCurrentPage(finalPage.getNumber());
		response.setPageSize(finalPage.getSize());
		response.setTotalElements(finalPage.getTotalElements());
		response.setTotalPages(finalPage.getTotalPages());

		List<CustomerBasicInfoDto> customerDtos = customers.stream()
		        .filter(c -> matchedCIds.contains(c.getCId()))
		        .map(c -> {
		            CustomerBasicInfoDto dto = new CustomerBasicInfoDto();
		            dto.setCId(c.getCId());
		            dto.setName(c.getName());
		            dto.setEmail(c.getEmail());
		            dto.setPhoneNumber(c.getPhoneNumber());
		            return dto;
		        }).toList();
		response.setCustomerEntities(customerDtos);


		// Step 7: Pharmacy + Store
		List<String> visitOrders = filteredVisits.stream().map(PatientVisitHistoryEntity::getVisitOrdNo).distinct()
				.toList();
		List<PatientPharmacy> patientPharmacyList = patientPharmacyRepository.findByVisitOrdNoIn(visitOrders);
		response.setPatientPharmacies(patientPharmacyList);

		List<String> storeIds = patientPharmacyList.stream().map(PatientPharmacy::getPharmacyStoreId)
				.filter(Objects::nonNull).distinct().toList();
		List<StoreBasicInfoDto> storeInfoDtos = storeRepository.findByIds(storeIds).stream().map(store -> {
			StoreBasicInfoDto dto = new StoreBasicInfoDto();
			dto.setId(store.getId());
			dto.setName(store.getName());
			dto.setLocation(store.getLocation());
			dto.setOwnerContact(store.getOwnerContact());
			dto.setOwnerEmail(store.getOwnerEmail());
			dto.setUserId(store.getUserId());
			return dto;
		}).toList();
		response.setPharmacyStoreId(storeInfoDtos);

		List<PatientDiagnostic> patientDiagnosticList = patientDiagnosticRepository.findByVisitOrdNoIn(visitOrders);
		response.setPatientDiagnostics(patientDiagnosticList);
		
		List<String> storeIdss = patientDiagnosticList.stream().map(PatientDiagnostic::getDiagnosticStoreId)
				.filter(Objects::nonNull).distinct().toList();
		List<StoreBasicInfoDto> storeInfoDtoss = storeRepository.findByIds(storeIdss).stream().map(store -> {
			StoreBasicInfoDto dto = new StoreBasicInfoDto();
			dto.setId(store.getId());
			dto.setName(store.getName());
			dto.setLocation(store.getLocation());
			dto.setOwnerContact(store.getOwnerContact());
			dto.setOwnerEmail(store.getOwnerEmail());
			dto.setUserId(store.getUserId());
			return dto;
		}).toList();
		response.setDiagnosticStoreId(storeInfoDtoss);
		return ResponseEntity.ok(response);
	}

}
