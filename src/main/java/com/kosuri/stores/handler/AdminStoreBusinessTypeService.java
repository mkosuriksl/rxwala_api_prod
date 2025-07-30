package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.AdminEntity;
import com.kosuri.stores.dao.AdminRepository;
import com.kosuri.stores.dao.AdminStoreBusinessTypeEntity;
import com.kosuri.stores.dao.AdminStoreBusinessTypeRepository;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.ResponseAdminStoreBusinessTypeDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdminStoreBusinessTypeService {

	@Autowired
	private AdminStoreBusinessTypeRepository adminStoreBusinessTypeRepository;

	@Autowired
	private AdminRepository adminRepository;
	
	@PersistenceContext
	private EntityManager entityManager;

	public ResponseAdminStoreBusinessTypeDto createBusinessType(AdminStoreBusinessTypeEntity entity) {
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<AdminEntity> login = adminRepository.findByEmailId(loggedInUserEmail);
	    
	    if (login.isEmpty()) {
	        throw new ResourceNotFoundException("Access denied. This API is restricted to admin users only.");
	    }

	    if (adminStoreBusinessTypeRepository.existsByBusinessTypeId(entity.getBusinessTypeId())) {
	        throw new ResourceNotFoundException("Business Type ID already exists: " + entity.getBusinessTypeId());
	    }

	    entity.setUpdatedDate(LocalDateTime.now());
	    entity.setUpdatedBy(login.get().getUpdatedBy());

	    AdminStoreBusinessTypeEntity savedEntity = adminStoreBusinessTypeRepository.save(entity);
	    return new ResponseAdminStoreBusinessTypeDto("Business type added successfully", savedEntity);
	}

	public List<AdminStoreBusinessTypeEntity> getAdminStoreBusinessType(String businessTypeId,String businessName,Map<String, String> requestParams) {
		
		List<String> expectedParams = Arrays.asList("businessTypeId","businessName");
	    for (String paramName : requestParams.keySet()) {
	        if (!expectedParams.contains(paramName)) {
	            throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
	        }
	    }
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminStoreBusinessTypeEntity> query = cb.createQuery(AdminStoreBusinessTypeEntity.class);
		Root<AdminStoreBusinessTypeEntity> root = query.from(AdminStoreBusinessTypeEntity.class);
		List<Predicate> predicates = new ArrayList<>();
		
		if (businessTypeId != null) {
			predicates.add(cb.equal(root.get("businessTypeId"), businessTypeId));
		}	
		if (businessName != null) {
			predicates.add(cb.equal(root.get("businessName"), businessName));
		}
		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

	public AdminStoreBusinessTypeEntity updateAdminStoreBusinessType(AdminStoreBusinessTypeEntity dto) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<AdminEntity> login = adminRepository.findByEmailId(loggedInUserEmail);
	    
	    if (login.isEmpty()) {
	        throw new ResourceNotFoundException("Access denied. This API is restricted to admin users only.");
	    }
		Optional<AdminStoreBusinessTypeEntity> idExists = adminStoreBusinessTypeRepository.findById(dto.getId());
		if (idExists.isPresent()) {
			AdminStoreBusinessTypeEntity Db = idExists.get();
			Db.setUpdatedBy(login.get().getUpdatedBy());
			Db.setBusinessTypeId(dto.getBusinessTypeId());
			Db.setBusinessName(dto.getBusinessName());
			return adminStoreBusinessTypeRepository.save(Db);
		}
		return null;
	}
}
