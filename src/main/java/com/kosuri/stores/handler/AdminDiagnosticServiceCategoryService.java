package com.kosuri.stores.handler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.AdminDiagnosticServiceCategory;
import com.kosuri.stores.dao.AdminDiagnosticServiceCategoryRepo;
import com.kosuri.stores.dao.AdminEntity;
import com.kosuri.stores.dao.AdminRepository;
import com.kosuri.stores.dao.PrimaryCareEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdminDiagnosticServiceCategoryService {
	
	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private AdminDiagnosticServiceCategoryRepo adminDiagnosticServiceCategoryRepo;
	
	@PersistenceContext
	private EntityManager entityManager;

	public List<AdminDiagnosticServiceCategory> addAdminDiagnosticServiceCategory(List<AdminDiagnosticServiceCategory> adminDiagnosticServiceCategorys) {
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<AdminEntity> login = adminRepository.findByEmailId(loggedInUserEmail);	    
	    List<AdminDiagnosticServiceCategory> adminDiagnosticServiceCategory = new ArrayList<>();
	    if (login.isEmpty()) {
	    	throw new ResourceNotFoundException("Access denied. This API is restricted to admin users only.");
	    }
	        for (AdminDiagnosticServiceCategory adsc : adminDiagnosticServiceCategorys) {
	        	AdminDiagnosticServiceCategory existingPlan = adminDiagnosticServiceCategoryRepo.findByDcServiceCategoryId(adsc.getDcServiceCategoryId());

	            if (existingPlan == null) {
	            	adsc.setUpdatedBy(login.get().getName()); 
//	            	adsc.setUpdatedBy(adminDiagnosticServiceCategorys.get(0).getUpdatedBy()); 
	            	adsc.setUpdatedDate(LocalDate.now());
	            	adminDiagnosticServiceCategory.add(adminDiagnosticServiceCategoryRepo.save(adsc));
	            
	        }
	    }
	    return adminDiagnosticServiceCategory;
	}

	public Page<AdminDiagnosticServiceCategory> getAdminDiagnosticServiceCategory(String dcServiceCategoryId,
			String dcServiceCategoryName, String updatedBy, Map<String, String> requestParams,
			Pageable pageable) {
		List<String> expectedParams = Arrays.asList(
				"dcServiceCategoryId", "dcServiceCategoryName", "updatedBy");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminDiagnosticServiceCategory> query = cb.createQuery(AdminDiagnosticServiceCategory.class);
		Root<AdminDiagnosticServiceCategory> root = query.from(AdminDiagnosticServiceCategory.class);
		List<Predicate> predicates = new ArrayList<>();
		if (dcServiceCategoryId != null) {
			predicates.add(cb.equal(root.get("dcServiceCategoryId"), dcServiceCategoryId));
		}
		if (dcServiceCategoryName != null) {
			predicates.add(cb.equal(root.get("dcServiceCategoryName"), dcServiceCategoryName));
		}
		if (updatedBy != null) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}
//		query.where(predicates.toArray(new Predicate[0]));

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<AdminDiagnosticServiceCategory> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());
		
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<AdminDiagnosticServiceCategory> countRoot = countQuery.from(AdminDiagnosticServiceCategory.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (dcServiceCategoryId != null) {
			countPredicates.add(cb.equal(countRoot.get("dcServiceCategoryId"), dcServiceCategoryId));
		}
		if (dcServiceCategoryName != null) {
			countPredicates.add(cb.equal(countRoot.get("dcServiceCategoryName"), dcServiceCategoryName));
		}
		if (updatedBy != null) {
			countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
		}
//		return entityManager.createQuery(query).getResultList();

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
		
	}

	public List<String> getDcServiceCategoryHomeDistinct() {
		return adminDiagnosticServiceCategoryRepo.findDcServiceCategoryName();
	}
	
	public Map<String, Object> getServiceCategoryBySearch(String dcServiceCategoryName) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
	    Root<AdminDiagnosticServiceCategory> root = query.from(AdminDiagnosticServiceCategory.class);

	    List<Predicate> predicates = new ArrayList<>();
	    if (dcServiceCategoryName != null && !dcServiceCategoryName.isBlank()) {
	        predicates.add(cb.like(cb.lower(root.get("dcServiceCategoryName")), "%" + dcServiceCategoryName.toLowerCase() + "%"));
	    }

	    query.multiselect(root.get("dcServiceCategoryName"));
	    query.where(cb.and(predicates.toArray(new Predicate[0])));

	    List<Object[]> resultList = entityManager.createQuery(query).getResultList();

	    List<Map<String, Object>> responseData = resultList.stream().map(obj -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("dcServiceCategoryName", obj[0]);
	        return map;
	    }).collect(Collectors.toList());

	    return Map.of(
	        "message", "AdminDiagnosticServiceCategory fetched successfully",
	        "status", true,
	        "totalCount", responseData.size(), // ✅ Total count added
	        "data", responseData
	    );
	}

	public List<AdminDiagnosticServiceCategory> updateAdminDiagnosticServiceCategory(
			List<AdminDiagnosticServiceCategory> updateAdminDiagnosticServiceCategories) {
		 String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		    Optional<AdminEntity> login = adminRepository.findByEmailId(loggedInUserEmail);	 
		 List<AdminDiagnosticServiceCategory> updatedRecords = new ArrayList<>();
		 if (login.isEmpty()) {
		    	throw new ResourceNotFoundException("Access denied. This API is restricted to admin users only.");
		    }

	        for (AdminDiagnosticServiceCategory updateAdminDiagnosticServiceCategory : updateAdminDiagnosticServiceCategories) {
	        	AdminDiagnosticServiceCategory existingRecord = adminDiagnosticServiceCategoryRepo.findById(updateAdminDiagnosticServiceCategory.getDcServiceCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Dc Service Category Id is not  Found By : " + updateAdminDiagnosticServiceCategory.getDcServiceCategoryId()));
	            if (existingRecord != null) {
	                existingRecord.setDcServiceCategoryName(updateAdminDiagnosticServiceCategory.getDcServiceCategoryName());
	                existingRecord.setUpdatedBy(login.get().getName());
	                adminDiagnosticServiceCategoryRepo.save(existingRecord);
	                updatedRecords.add(existingRecord);
	            }
	        }
	        return updatedRecords;
	}

}
