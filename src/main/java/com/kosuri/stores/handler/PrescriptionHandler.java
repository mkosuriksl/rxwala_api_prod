package com.kosuri.stores.handler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.Prescription;
import com.kosuri.stores.dao.PrescriptionHistory;
import com.kosuri.stores.dao.PrescriptionHistoryRepository;
import com.kosuri.stores.dao.PrescriptionRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.dao.VisitPrescriptionGroupDto;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.GenericResponse;
import com.kosuri.stores.model.dto.PrescriptionHistoryRequest;
import com.kosuri.stores.model.dto.PrescriptionRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class PrescriptionHandler {
	
	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private PrescriptionHistoryRepository prescriptionHistoryRepository;
	
	@Autowired
	private TabStoreRepository tabStoreRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	

//	public GenericResponse<Prescription> savePrescription(Prescription prescription) {
//		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//
//		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
//		if (loginStore.isEmpty()) {
//			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
//		}
//	    Optional<Prescription> existing = prescriptionRepository.findById(prescription.getVisitOrdNo());
//
//	    if (existing.isPresent()) {
//	        return new GenericResponse<>("false", "Prescription with visitOrdNo already exists", null);
//	    }
//
//	    prescription.setUpdatedDate(LocalDateTime.now());
//	    prescription.setUpdatedBy(loginStore.get().getUserId());
//	    Prescription saved = prescriptionRepository.save(prescription);
//	    return new GenericResponse<>("true", "Prescription saved successfully", saved);
//	}
	
	public GenericResponse<List<Prescription>> savePrescription(PrescriptionRequest request) {
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);

	    if (loginStore.isEmpty()) {
	        throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
	    }

	    // Optional: Check if prescription exists already for visitOrdNo
	    Optional<Prescription> existing = prescriptionRepository.findById(request.getVisitOrdNo());
	    if (existing.isPresent()) {
	        return new GenericResponse<>("false", "Prescription with visitOrdNo already exists", null);
	    }

	    List<Prescription> prescriptionsToSave = new ArrayList<>();

	    for (PrescriptionHistoryRequest history : request.getHistoryRequests()) {
	        Prescription prescription = new Prescription();
	        prescription.setVisitOrdNo(request.getVisitOrdNo());
	        prescription.setUserId(loginStore.get().getUserId());
	        prescription.setStoreId(request.getStoreId());
	        prescription.setUserIdStoreId(loginStore.get().getUserId() + "_" + request.getStoreId());
	        prescription.setMedicineName(history.getMedicineName());
	        prescription.setMorningQty(history.getMorningQty());
	        prescription.setAfternoonQty(history.getAfternoonQty());
	        prescription.setNightQty(history.getNightQty());
	        prescription.setBeforeFood(history.isBeforeFood());
	        prescription.setAfterFood(history.isAfterFood());
	        prescription.setVisitingDate(LocalDateTime.now());
	        prescription.setUpdatedDate(LocalDateTime.now());
	        prescription.setUpdatedBy(loginStore.get().getUserId());

	        prescriptionsToSave.add(prescription);
	    }

	    List<Prescription> savedList = prescriptionRepository.saveAll(prescriptionsToSave);
	    return new GenericResponse<>("true", "Prescription saved successfully", savedList);
	}



//	public GenericResponse<Prescription> updatePrescription(Prescription updated) {
//		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//
//		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
//		if (loginStore.isEmpty()) {
//			throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
//		}
//		String visitOrdNo = updated.getVisitOrdNo();
//
//		Prescription existing = prescriptionRepository.findById(visitOrdNo)
//				.orElseThrow(() -> new RuntimeException("Prescription not found for visitOrdNo: " + visitOrdNo));
//
//		// Copy to history
//		PrescriptionHistory history = new PrescriptionHistory();
//		history.setVisitOrdNo(visitOrdNo);
//		history.setMedicineName(existing.getMedicineName());
//		history.setMorningQty(existing.getMorningQty());
//		history.setAfternoonQty(existing.getAfternoonQty());
//		history.setNightQty(existing.getNightQty());
//		history.setBeforeFood(existing.isBeforeFood());
//		history.setAfterFood(existing.isAfterFood());
//		history.setVisitingDate(existing.getVisitingDate());
//		history.setUserId(existing.getUserId());
//		history.setUserIdStoreId(existing.getUserIdStoreId());
//		history.setUpdatedBy(existing.getUpdatedBy());
//		history.setUpdatedDate(existing.getUpdatedDate());
//		history.setStoreId(existing.getStoreId());
//
//		// Generate visitOrdNo_lineId
//		long count = prescriptionHistoryRepository.countByVisitOrdNo(visitOrdNo);
//		String lineId = String.format("%s_%04d", visitOrdNo, count + 1);
//		history.setVisitOrdNoLineId(lineId);
//
//		prescriptionHistoryRepository.save(history);
//
//		// Update new
//		updated.setUpdatedDate(LocalDateTime.now());
//		updated.setUpdatedBy(loginStore.get().getUserId());
//		Prescription saved = prescriptionRepository.save(updated);
//		return new GenericResponse<>("true", "Prescription updated and history stored", saved);
//	}
	
	public GenericResponse<List<Prescription>> updatePrescriptions(List<Prescription> updates) {
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();

	    Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
	    if (loginStore.isEmpty()) {
	        throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
	    }

	    List<Prescription> savedList = new ArrayList<>();

	    for (Prescription updated : updates) {
	        String visitOrdNo = updated.getVisitOrdNo();
	        Optional<Prescription> existingOpt = prescriptionRepository
	                .findByVisitOrdNo(visitOrdNo);

	        if (existingOpt.isEmpty()) {
	            throw new ResourceNotFoundException("Prescription not found for " + visitOrdNo);
	        }

	        Prescription existing = existingOpt.get();

	        // Copy to history
	        PrescriptionHistory history = new PrescriptionHistory();
	        history.setVisitOrdNo(visitOrdNo);
	        history.setMedicineName(existing.getMedicineName());
	        history.setMorningQty(existing.getMorningQty());
	        history.setAfternoonQty(existing.getAfternoonQty());
	        history.setNightQty(existing.getNightQty());
	        history.setBeforeFood(existing.isBeforeFood());
	        history.setAfterFood(existing.isAfterFood());
	        history.setVisitingDate(existing.getVisitingDate());
	        history.setUserId(existing.getUserId());
	        history.setUserIdStoreId(existing.getUserIdStoreId());
	        history.setUpdatedBy(existing.getUpdatedBy());
	        history.setUpdatedDate(existing.getUpdatedDate());
	        history.setStoreId(existing.getStoreId());

	        long count = prescriptionHistoryRepository.countByVisitOrdNo(visitOrdNo);
	        String lineId = String.format("%s_%04d", visitOrdNo, count + 1);
	        history.setVisitOrdNoLineId(lineId);
	        prescriptionHistoryRepository.save(history);

	        // Update fields
	        existing.setMorningQty(updated.getMorningQty());
	        existing.setAfternoonQty(updated.getAfternoonQty());
	        existing.setNightQty(updated.getNightQty());
	        existing.setUpdatedBy(loginStore.get().getUserId());
	        existing.setUpdatedDate(LocalDateTime.now());

	        savedList.add(existing);
	    }

	    List<Prescription> finalSaved = prescriptionRepository.saveAll(savedList);
	    return new GenericResponse<>("true", "Prescriptions updated and history stored", finalSaved);
	}

	
	public Page<PrescriptionHistory> getPrescriptionHistory(String visitOrdNoLineId, String visitOrdNo, String medicineName,
			String userId,String userIdStoreId,Pageable pageable) throws AccessDeniedException {
		

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PrescriptionHistory> query = cb.createQuery(PrescriptionHistory.class);
		Root<PrescriptionHistory> root = query.from(PrescriptionHistory.class);
		List<Predicate> predicates = new ArrayList<>();

		if (visitOrdNoLineId != null && !visitOrdNoLineId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("visitOrdNoLineId"), visitOrdNoLineId));
		}
		if (visitOrdNo != null && !visitOrdNo.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("visitOrdNo"), visitOrdNo));
		}
		if (medicineName != null && !medicineName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("medicineName"), medicineName));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}
		if (userIdStoreId != null && !userIdStoreId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<PrescriptionHistory> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<PrescriptionHistory> countRoot = countQuery.from(PrescriptionHistory.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (visitOrdNoLineId != null && !visitOrdNoLineId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("visitOrdNoLineId"), visitOrdNoLineId));
		}
		if (visitOrdNo != null && !visitOrdNo.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("visitOrdNo"), visitOrdNo));
		}
		if (medicineName != null && !medicineName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("medicineName"), medicineName));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userId"), userId));
		}
		if (userIdStoreId != null && !userIdStoreId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userIdStoreId"), userIdStoreId));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}
	
	public Page<Prescription> getPrescription(String visitOrdNo, String medicineName,
			String userId,String userIdStoreId,Pageable pageable) throws AccessDeniedException {
		

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Prescription> query = cb.createQuery(Prescription.class);
		Root<Prescription> root = query.from(Prescription.class);
		List<Predicate> predicates = new ArrayList<>();

		if (visitOrdNo != null && !visitOrdNo.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("visitOrdNo"), visitOrdNo));
		}
		if (medicineName != null && !medicineName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("medicineName"), medicineName));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}
		if (userIdStoreId != null && !userIdStoreId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<Prescription> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Prescription> countRoot = countQuery.from(Prescription.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (visitOrdNo != null && !visitOrdNo.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("visitOrdNo"), visitOrdNo));
		}
		if (medicineName != null && !medicineName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("medicineName"), medicineName));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userId"), userId));
		}
		if (userIdStoreId != null && !userIdStoreId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userIdStoreId"), userIdStoreId));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}
	
	public Page<VisitPrescriptionGroupDto> getGroupedPrescriptionHistory(
	        String visitOrdNo, String medicineName, String userId,
	        String userIdStoreId, Pageable pageable) {

	    // Step 1: Fetch distinct visitOrdNos for pagination
	    Page<String> visitOrdNoPage = prescriptionHistoryRepository.findByVisitOrdNo(
	        visitOrdNo, medicineName, userId, userIdStoreId, pageable
	    );

	    // Step 2: For each visitOrdNo, fetch matching records
	    List<VisitPrescriptionGroupDto> groupedList = visitOrdNoPage.getContent().stream()
	        .map(vOrdNo -> {
	            List<PrescriptionHistory> records = prescriptionHistoryRepository
	                .findByVisitOrdNo(vOrdNo);
	            VisitPrescriptionGroupDto dto = new VisitPrescriptionGroupDto();
	            dto.setVisitOrdNo(vOrdNo);
	            dto.setPrescriptionOldHistory(records);
	            return dto;
	        }).toList();

	    return new PageImpl<>(groupedList, pageable, visitOrdNoPage.getTotalElements());
	}

}
