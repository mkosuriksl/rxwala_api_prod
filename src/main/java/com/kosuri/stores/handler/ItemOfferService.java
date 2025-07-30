package com.kosuri.stores.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.ItemOfferEntity;
import com.kosuri.stores.dao.ItemOfferHistoryEntity;
import com.kosuri.stores.dao.ItemOfferHistoryRepository;
import com.kosuri.stores.dao.ItemOfferRepository;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StockRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.ApiResponse;
import com.kosuri.stores.model.dto.ItemOfferDTO;
import com.kosuri.stores.model.dto.ItemOfferRequestDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ItemOfferService {

	@Autowired
	private ItemOfferRepository itemOfferRepository;

	@Autowired
	private ItemOfferHistoryRepository historyRepository;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public ItemOfferEntity addOffer(ItemOfferEntity entity) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		List<StockEntity> stocks = stockRepository.findAllByUserIdStoreIdItemCode(entity.getUserIdStoreIdItemCode());
		if (!stocks.isEmpty()) {
			entity.setUserIdStoreIdItemCode(stocks.get(0).getUserIdStoreIdItemCode()); // use the first one or apply
																						// logic
		}
		entity.setUpdatedBy(loginStore.get().getUserId());
		entity.setUpdatedDate(new Date());
		return itemOfferRepository.save(entity);
	}

	public ResponseEntity<ApiResponse<List<ItemOfferEntity>>> saveItemOffers(ItemOfferRequestDTO requestDto) {
		List<ItemOfferEntity> savedEntities = new ArrayList<>();

		for (ItemOfferDTO dto : requestDto.getItemOffers()) {
			ItemOfferEntity entity = new ItemOfferEntity();
			entity.setUserId(requestDto.getUserId());
			entity.setStoreId(requestDto.getStoreId());
			entity.setUserIdStoreId(requestDto.getUserIdStoreId());
			entity.setUserIdStoreIdItemCode(dto.getUserIdStoreId_itemCode());
			entity.setBatchNumber(dto.getBatchNumber());
			entity.setDiscount(dto.getDiscount());
			entity.setOfferQty(dto.getOfferQty());
			entity.setMinOrderQty(dto.getMinOrderQty());
			entity.setUpdatedBy(requestDto.getUserId());
			entity.setUpdatedDate(new Date());

			itemOfferRepository.save(entity);
			savedEntities.add(entity);
		}

		ApiResponse<List<ItemOfferEntity>> response = new ApiResponse<>("Item offers saved successfully", true,
				savedEntities);

		return ResponseEntity.ok(response);
	}

	public List<ItemOfferEntity> updateOffer(List<ItemOfferEntity> offerList) {
		// 1. Validate logged-in user
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}

		List<ItemOfferEntity> updatedList = new ArrayList<>();

		// 2. Find existing record
		for (ItemOfferEntity newData : offerList) {
			ItemOfferEntity existing = itemOfferRepository
					.findByUserIdStoreIdItemCode(newData.getUserIdStoreIdItemCode()).orElseThrow(
							() -> new RuntimeException("Offer not found for: " + newData.getUserIdStoreIdItemCode()));

			// 3. Save current record into history
			ItemOfferHistoryEntity history = new ItemOfferHistoryEntity();
			BeanUtils.copyProperties(existing, history);
			historyRepository.save(history);

			// 4. Update main table fields
			existing.setBatchNumber(newData.getBatchNumber());
			existing.setDiscount(newData.getDiscount());
			existing.setOfferQty(newData.getOfferQty());
			existing.setMinOrderQty(newData.getMinOrderQty());
			existing.setUpdatedBy(loginStore.get().getUserId()); // ✅ Use logged-in user
			existing.setUpdatedDate(new Date());

			updatedList.add(itemOfferRepository.save(existing));
//		return itemOfferRepository.save(existing);
		}
		return updatedList;
	}

	public List<ItemOfferEntity> getOffer(String userIdStoreIdItemCode, String batchNumber, String discount,
			String userId, String storeId, String userIdStoreId, Map<String, String> requestParams) {

		List<String> expectedParams = Arrays.asList("userIdStoreIdItemCode", "batchNumber", "discount", "userId",
				"storeId", "userIdStoreId");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ItemOfferEntity> query = cb.createQuery(ItemOfferEntity.class);
		Root<ItemOfferEntity> root = query.from(ItemOfferEntity.class);
		List<Predicate> predicates = new ArrayList<>();

		if (userIdStoreIdItemCode != null) {
			predicates.add(cb.equal(root.get("userIdStoreIdItemCode"), userIdStoreIdItemCode));
		}
		if (batchNumber != null) {
			predicates.add(cb.equal(root.get("batchNumber"), batchNumber));
		}
		if (discount != null) {
			predicates.add(cb.equal(root.get("discount"), discount));
		}
		if (userId != null) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}
		if (storeId != null) {
			predicates.add(cb.equal(root.get("storeId"), storeId));
		}
		if (userIdStoreId != null) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}
		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

}
