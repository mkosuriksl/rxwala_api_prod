package com.kosuri.stores.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.ItemDiscountCurrent;
import com.kosuri.stores.dao.ItemDiscountCurrentRepository;
import com.kosuri.stores.dao.ItemDiscountHistory;
import com.kosuri.stores.dao.ItemDiscountHistoryRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ItemDiscountCurrentService {

	@Autowired
	private ItemDiscountCurrentRepository itemDiscountCurrentRepository;
	
	@Autowired
	private ItemDiscountHistoryRepository itemDiscountHistoryRepository;

	@Autowired
	private StoreRepository storeRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	public ItemDiscountCurrent createItemDiscountCurrent(ItemDiscountCurrent itemDiscountCurrentRequest) {

		 String loggedInEmail = AuthDetailsProvider.getLoggedEmail();
		    Optional<TabStoreUserEntity> userEntityOptional = tabStoreRepository.findByStoreUserEmail(loggedInEmail);

		    if (userEntityOptional.isPresent()) {
		    	StoreEntity storeEntity = storeRepository.findByUserIdStoreId(itemDiscountCurrentRequest.getUserIdStoreId())
						.orElseThrow(() -> new ResourceNotFoundException(
								"UserIdStoreId is not found for StoreEntity: " + itemDiscountCurrentRequest.getUserIdStoreId()));
		    	itemDiscountCurrentRequest.setUserIdStoreId(storeEntity.getUserIdStoreId());
		    	itemDiscountCurrentRequest.setUpdatedBy(userEntityOptional.get().getUserId());
		    	itemDiscountCurrentRequest.setItemCode(itemDiscountCurrentRequest.getItemCode());;
		    	itemDiscountCurrentRequest.setDiscount(itemDiscountCurrentRequest.getDiscount());

			    return itemDiscountCurrentRepository.save(itemDiscountCurrentRequest);
			    } else {
			        throw new RuntimeException("User not found for email: " + loggedInEmail);
			    }
	}
	
	@Transactional
    public void updateItemDiscount(ItemDiscountCurrent itemDiscountCurrentRequest) {
        String userIdStoreIdSkuId = itemDiscountCurrentRequest.getUserIdStoreIdItemCode();

        // Fetch existing record
        Optional<ItemDiscountCurrent> existingRecordOpt = itemDiscountCurrentRepository.findById(userIdStoreIdSkuId);

        if (existingRecordOpt.isPresent()) {
            ItemDiscountCurrent existingRecord = existingRecordOpt.get();

            // Save to history before updating
            ItemDiscountHistory history = new ItemDiscountHistory();
            history.setUserIdStoreIdItemCode(existingRecord.getUserIdStoreIdItemCode());
            history.setItemCode(existingRecord.getItemCode());
            history.setUserIdStoreId(existingRecord.getUserIdStoreId());
            history.setDiscount(existingRecord.getDiscount());
            history.setUpdatedBy(existingRecord.getUpdatedBy());
            history.setUpdatedDate(existingRecord.getUpdatedDate());

            itemDiscountHistoryRepository.save(history);

            // Update current record
            existingRecord.setDiscount(itemDiscountCurrentRequest.getDiscount());
            itemDiscountCurrentRepository.save(existingRecord);
        } else {
            throw new EntityNotFoundException("ItemDiscountCurrent record not found for ID: " + userIdStoreIdSkuId);
        }
    }

	public List<ItemDiscountCurrent> getItemDiscount(String userIdStoreIdItemCode,String itemCode,Integer discount,String updatedBy,Map<String, String> requestParams) {
		
		List<String> expectedParams = Arrays.asList("userIdStoreIdItemCode","itemCode","discount","updatedBy");
	    for (String paramName : requestParams.keySet()) {
	        if (!expectedParams.contains(paramName)) {
	            throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
	        }
	    }
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ItemDiscountCurrent> query = cb.createQuery(ItemDiscountCurrent.class);
		Root<ItemDiscountCurrent> root = query.from(ItemDiscountCurrent.class);
		List<Predicate> predicates = new ArrayList<>();
		
		if (userIdStoreIdItemCode != null) {
			predicates.add(cb.equal(root.get("userIdStoreIdItemCode"), userIdStoreIdItemCode));
		}
		if (itemCode != null) {
			predicates.add(cb.equal(root.get("itemCode"), itemCode));
		}
		if (discount != null) {
			predicates.add(cb.equal(root.get("discount"), discount));
		}
		if (updatedBy != null) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}
		
		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

}
