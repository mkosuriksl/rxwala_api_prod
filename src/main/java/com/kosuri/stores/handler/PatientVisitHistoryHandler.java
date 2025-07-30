package com.kosuri.stores.handler;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.PatientDiagnostic;
import com.kosuri.stores.dao.PatientDiagnosticRepository;
import com.kosuri.stores.dao.PatientPharmacy;
import com.kosuri.stores.dao.PatientPharmacyRepository;
import com.kosuri.stores.dao.PatientVisitHistoryEntity;
import com.kosuri.stores.dao.PatientVisitHistoryRepository;
import com.kosuri.stores.model.request.PatientVisitHistoryRequest;
import com.kosuri.stores.model.response.GenericResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PatientVisitHistoryHandler {
	
	@Autowired
	private PatientDiagnosticRepository patientDiagnosticRepository;

	@Autowired
	private PatientPharmacyRepository patientPharmacyRepository;
	
	@Autowired
	private PatientVisitHistoryRepository patientVisitHistoryRepository;

	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;

	@Autowired
	private ModelMapper mapper;

	@PersistenceContext
	private EntityManager entityManager;

//	@Transactional
//	public CustomerVisitHistories getAllPatientVisitHistoryByCustomerId(String cid) {
//		log.info(">>Service Logger getAllPatientVisitHistoryByCustomerId({})", cid);
//
//		CustomerVisitHistories cvh = new CustomerVisitHistories();
//		List<PatientVisitHistoryEntity> historyEntities = patientVisitHistoryRepository.findByCid(cid);
//
//		if (historyEntities.isEmpty()) {
//			log.warn("No visit history found for customer id: {}", cid);
//			// Handle the case where no records are found, e.g., throw an exception or
//			// return a default response.
//			return cvh; // Or throw new EntityNotFoundException("No visit history found for customer id:
//						// " + cid);
//		}
//
//		CustomerRegisterEntity customerRegisterEntity = historyEntities.get(0).getCustomerRegisterEntity();
//		cvh.setCustomerDetail(customerRegisterEntity);
//		List<CustomerVisitResponse> visitHistories = historyEntities.stream()
//				.map(visit -> mapper.map(visit, CustomerVisitResponse.class)).collect(Collectors.toList());
//
//		cvh.setVisitHistories(visitHistories);
//		return cvh;
//	}

//	@Transactional
//	public PatientVisitHistoryEntity getPatientVisitHistoryByCidAndById(String cid, String visitOrdNo) {
//		log.info(">>Service Logger getPatientVisitHistoryByCidAndById({})", cid, visitOrdNo);
//		return patientVisitHistoryRepository.findByCidAndVisitOrdNo(cid, visitOrdNo)
//				.orElseThrow(() -> new RuntimeException("Patient Visit History By Visit No : " + visitOrdNo));
//	}

	@Transactional
	public GenericResponse savePatientVisitHistory(PatientVisitHistoryRequest patientVisitHistoryRequest) {
		log.info(">>Service Logger savePatientVisitHistory({})", patientVisitHistoryRequest);
		GenericResponse response = new GenericResponse();
		PatientVisitHistoryEntity entity = mapToPatientVisitHistoryntity(patientVisitHistoryRequest);
		CustomerRegisterEntity customerRegisterEntity = customerRegisterRepository
				.findByCid(patientVisitHistoryRequest.getCid()).orElseThrow(() -> new RuntimeException(
						"Customer Not Found By ID : " + patientVisitHistoryRequest.getCid()));
		entity.setCId(customerRegisterEntity.getCId());

		patientVisitHistoryRepository.save(entity);
		response.setResponseMessage("Patient Visit History created successfully!");
		return response;
	}

	private PatientVisitHistoryEntity mapToPatientVisitHistoryntity(PatientVisitHistoryRequest request) {
		PatientVisitHistoryEntity entity = new PatientVisitHistoryEntity();
		entity.setVisitingDate(request.getVisitingDate());
		entity.setCauseOfVisit(request.getCauseOfVisit());
		entity.setMedication("Y");
		entity.setTreatedBy(request.getTreatedBy());
		entity.setReferredTo(request.getReferredTo());
		return entity;
	}

	public Page<PatientVisitHistoryEntity> get(String visitOrdNo, String cId, String name, String email, String phoneNumber, String pharmacyStoreId,String diagnosticStoreId, Pageable pageable) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // Step 1: Filter customers (cId, name, email, phoneNumber)
	    List<String> matchedCIds = null;
	    boolean customerFiltersPresent = (name != null && !name.isBlank()) ||
	                                     (email != null && !email.isBlank()) ||
	                                     (phoneNumber != null && !phoneNumber.isBlank());

	    if (cId != null || customerFiltersPresent) {
	        CriteriaQuery<String> cidQuery = cb.createQuery(String.class);
	        Root<CustomerRegisterEntity> customerRoot = cidQuery.from(CustomerRegisterEntity.class);

	        List<Predicate> customerPredicates = new ArrayList<>();
	        if (cId != null && !cId.isBlank()) {
	            customerPredicates.add(cb.equal(customerRoot.get("cId"), cId));
	        }
	        if (name != null && !name.isBlank()) {
	            customerPredicates.add(cb.like(cb.lower(customerRoot.get("name")), "%" + name.toLowerCase() + "%"));
	        }
	        if (email != null && !email.isBlank()) {
	            customerPredicates.add(cb.like(cb.lower(customerRoot.get("email")), "%" + email.toLowerCase() + "%"));
	        }
	        if (phoneNumber != null && !phoneNumber.isBlank()) {
	            customerPredicates.add(cb.like(customerRoot.get("phoneNumber"), "%" + phoneNumber + "%"));
	        }

	        cidQuery.select(customerRoot.get("cId")).where(cb.and(customerPredicates.toArray(new Predicate[0])));
	        matchedCIds = entityManager.createQuery(cidQuery).getResultList();

	        // If no match found, exit early
	        if (matchedCIds == null || matchedCIds.isEmpty()) {
	            return Page.empty();
	        }
	    }

	    // Step 2: Filter visit records
	    CriteriaQuery<PatientVisitHistoryEntity> query = cb.createQuery(PatientVisitHistoryEntity.class);
	    Root<PatientVisitHistoryEntity> root = query.from(PatientVisitHistoryEntity.class);

	    List<Predicate> visitPredicates = new ArrayList<>();
	    if (visitOrdNo != null && !visitOrdNo.isBlank()) {
	        visitPredicates.add(cb.equal(root.get("visitOrdNo"), visitOrdNo));
	    }
	    if (matchedCIds != null) {
	        visitPredicates.add(root.get("cId").in(matchedCIds));
	    } else if (cId != null && !cId.isBlank()) {
	        visitPredicates.add(cb.equal(root.get("cId"), cId));
	    }

	    query.select(root).where(cb.and(visitPredicates.toArray(new Predicate[0])));
	    TypedQuery<PatientVisitHistoryEntity> typedQuery = entityManager.createQuery(query);
	    List<PatientVisitHistoryEntity> results = typedQuery.getResultList();

	    if (results.isEmpty()) {
	        return Page.empty();
	    }

	    // Step 3: Filter by pharmacyStoreId (strict check)
	    if (pharmacyStoreId != null && !pharmacyStoreId.isBlank()) {
	        List<String> matchingVisitNos = patientPharmacyRepository
	                .findByVisitOrdNoIn(results.stream().map(PatientVisitHistoryEntity::getVisitOrdNo).toList())
	                .stream()
	                .filter(ph -> pharmacyStoreId.equals(ph.getPharmacyStoreId()))
	                .map(PatientPharmacy::getVisitOrdNo)
	                .toList();

	        // Keep only visits with matching pharmacy
	        results = results.stream()
	                .filter(v -> matchingVisitNos.contains(v.getVisitOrdNo()))
	                .toList();

	        if (results.isEmpty()) {
	            return Page.empty();
	        }
	    }
	    
	    if (diagnosticStoreId != null && !diagnosticStoreId.isBlank()) {
	        List<String> matchingVisitNos = patientDiagnosticRepository
	                .findByVisitOrdNoIn(results.stream().map(PatientVisitHistoryEntity::getVisitOrdNo).toList())
	                .stream()
	                .filter(ph -> diagnosticStoreId.equals(ph.getDiagnosticStoreId()))
	                .map(PatientDiagnostic::getVisitOrdNo)
	                .toList();

	        // Keep only visits with matching pharmacy
	        results = results.stream()
	                .filter(v -> matchingVisitNos.contains(v.getVisitOrdNo()))
	                .toList();

	        if (results.isEmpty()) {
	            return Page.empty();
	        }
	    }


	    // Pagination (manual because of in-memory filtering)
	    int start = (int) pageable.getOffset();
	    int end = Math.min(start + pageable.getPageSize(), results.size());
	    List<PatientVisitHistoryEntity> pageContent = results.subList(start, end);

	    return new PageImpl<>(pageContent, pageable, results.size());
	}


}
