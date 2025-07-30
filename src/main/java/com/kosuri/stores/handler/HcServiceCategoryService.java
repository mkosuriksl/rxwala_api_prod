package com.kosuri.stores.handler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.HcServiceCategory;
import com.kosuri.stores.dao.HcServiceCategoryRepo;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class HcServiceCategoryService {

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@Autowired
	private HcServiceCategoryRepo hcServiceCategoryRepo;
	
	@Autowired
	private StoreRepository storeRepository;
	
	@PersistenceContext
	private EntityManager entityManager;

	public HcServiceCategory addHcServiceCategory(HcServiceCategory hcServiceCtgry) {
	    String loggedInEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<TabStoreUserEntity> userEntityOptional = tabStoreRepository.findByStoreUserEmail(loggedInEmail);

	    if (userEntityOptional.isPresent()) {
	        StoreEntity storeEntity = storeRepository.findById(hcServiceCtgry.getStoreId())
	                .orElseThrow(() -> new ResourceNotFoundException("StoreId not found in store info: " + hcServiceCtgry.getStoreId()));  // ✅ Proper exception

	        hcServiceCtgry.setStoreId(storeEntity.getId());
	        hcServiceCtgry.setUpdatedBy(userEntityOptional.get().getUserId());
	        hcServiceCtgry.setStatus("Active");

	        return hcServiceCategoryRepo.save(hcServiceCtgry);
	    } else {
	        throw new RuntimeException("User not found for email: " + loggedInEmail);
	    }
	}
	
	public HcServiceCategory updateHcServiceCategory(HcServiceCategory hcServiceCategory) {
		
		String loggedInEmail = AuthDetailsProvider.getLoggedEmail();
		   Optional<TabStoreUserEntity> userEntityOptional = tabStoreRepository.findByStoreUserEmail(loggedInEmail);
		   if (userEntityOptional.isPresent()) {
		        Optional<HcServiceCategory> idExists = hcServiceCategoryRepo.findById(hcServiceCategory.getUserIdStoreIdServicecategoryId());

		        if (idExists.isPresent()) {
		            HcServiceCategory db = idExists.get();
		            db.setStatus(hcServiceCategory.getStatus());
		            db.setStatusUpdatedBy(userEntityOptional.get().getUserId());
		            db.setStatusUpdatedDate(LocalDate.now());
		            return hcServiceCategoryRepo.save(db);
		        } else {
		            throw new RuntimeException("Service category not found for ID: " + hcServiceCategory.getUserIdStoreIdServicecategoryId());
		        }
		    } else {
		        throw new RuntimeException("User not found for email: " + loggedInEmail);
		    }
	}
	
public List<HcServiceCategory> getHcServiceCategory(String storeId,String serviceCategoryId,String serviceCategoryName,String userId,Map<String, String> requestParams) {
		
		List<String> expectedParams = Arrays.asList("storeId","serviceCategoryId","serviceCategoryName","userId");
	    for (String paramName : requestParams.keySet()) {
	        if (!expectedParams.contains(paramName)) {
	            throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
	        }
	    }
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<HcServiceCategory> query = cb.createQuery(HcServiceCategory.class);
		Root<HcServiceCategory> root = query.from(HcServiceCategory.class);
		List<Predicate> predicates = new ArrayList<>();
		
		if (storeId != null) {
			predicates.add(cb.equal(root.get("storeId"), storeId));
		}
		if (serviceCategoryId != null) {
			predicates.add(cb.equal(root.get("serviceCategoryId"), serviceCategoryId));
		}
		if (serviceCategoryName != null) {
			predicates.add(cb.equal(root.get("serviceCategoryName"), serviceCategoryName));
		}
		if (userId != null) {
			predicates.add(cb.equal(root.get("updatedBy"), userId));
		}
		
		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

	public List<String> getHcDistinctServiceCategory() {
	
	return hcServiceCategoryRepo.findByHcDistinctServiceCategory();
}

}
