package com.kosuri.stores.handler;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosuri.stores.dao.AdminStoreVerificationEntity;
import com.kosuri.stores.dao.ItemCodeMaster;
import com.kosuri.stores.dao.ItemCodeMasterImage;
import com.kosuri.stores.dao.ItemCodeMasterImageRepository;
import com.kosuri.stores.dao.ItemCodeMasterRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.GenericResponse;
import com.kosuri.stores.model.dto.ItemCodeSearchDTO;
import com.kosuri.stores.s3.config.AmazonS3Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ItemCodeMasterService {

	@Autowired
	private ItemCodeMasterRepository repository;

	@Autowired
	private ItemCodeMasterImageRepository itemCodeMasterImageRepository;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private AmazonS3Service amazonService;

	private static String folderName = "tablet-image-itemcodemaster/";

	public GenericResponse<List<ItemCodeMaster>> saveItems(List<ItemCodeMaster> items) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);

		if (login.isEmpty()) {
			return new GenericResponse<>("error", "Access denied. This API is restricted to store users only.", null);
		}

		String userId = login.get().getUserId();
		List<String> duplicateKeys = new ArrayList<>();
		List<ItemCodeMaster> itemsToSave = new ArrayList<>();

		for (ItemCodeMaster item : items) {
			item.setUserId(userId);
			item.setUserIdStoreId(userId + "_" + item.getStoreId());
			item.setUpdatedBy(userId);
			item.setUpdatedDate(new Date());

			// Prefix without itemCode
			String prefix = userId + "_" + item.getStoreId(); // or custom key instead of itemName

			// Find max existing IC number for this prefix
			String latestItemCode = repository.findMaxItemCodeByPrefix(prefix + "_IC");

			int nextSequence = 1;
			if (latestItemCode != null && latestItemCode.matches(".*IC\\d{4}$")) {
				String numberPart = latestItemCode.substring(latestItemCode.lastIndexOf("IC") + 2);
				nextSequence = Integer.parseInt(numberPart) + 1;
			}

			String newItemCode = String.format("IC%04d", nextSequence);
			item.setItemCode(newItemCode);

			// Full ID key
			String key = prefix + "_" + newItemCode;
			item.setUserIdStoreIdItemCode(key);

			if (repository.existsById(key)) {
				duplicateKeys.add(key);
			} else {
				itemsToSave.add(item);
			}
		}

		if (!duplicateKeys.isEmpty()) {
			return new GenericResponse<>("true",
					"Duplicate entries found for keys: " + String.join(", ", duplicateKeys), null);
		}

		List<ItemCodeMaster> savedItems = repository.saveAll(itemsToSave);
		return new GenericResponse<>("success", "Item code master list saved successfully", savedItems);
	}

	public List<ItemCodeMaster> updateItemNames(List<ItemCodeMaster> requestItems) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);

		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
		}

		String userId = login.get().getUserId();
		List<ItemCodeMaster> updatedItems = new ArrayList<>();

		for (ItemCodeMaster requestItem : requestItems) {
			String key = requestItem.getUserIdStoreIdItemCode();
			if (key == null || key.isBlank()) {
				continue; // Skip if ID is not valid
			}

			Optional<ItemCodeMaster> optionalItem = repository.findByUserIdStoreIdItemCode(key);
			if (optionalItem.isPresent()) {
				ItemCodeMaster item = optionalItem.get();
				item.setItemName(requestItem.getItemName());
				item.setBrand(requestItem.getBrand());
				item.setGst(requestItem.getGst());
				item.setItemCategory(requestItem.getItemCategory());
				item.setItemSubCategory(requestItem.getItemSubCategory());
				item.setManufacturer(requestItem.getManufacturer());
				item.setHsnGroup(requestItem.getHsnGroup());

				item.setUpdatedBy(userId);
				item.setUpdatedDate(new Date());

				ItemCodeMaster savedItem = repository.save(item);
				updatedItems.add(savedItem);
			}
		}

		return updatedItems;
	}

	public Page<ItemCodeMaster> get(String userIdStoreIdItemCode, String storeId, String itemCode, String itemName,
			String itemCategory, String itemSubCategory, String manufacturer, String brand, String hsnGroup,
			String userId, String userIdStoreId, Pageable pageable) throws AccessDeniedException {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ItemCodeMaster> query = cb.createQuery(ItemCodeMaster.class);
		Root<ItemCodeMaster> root = query.from(ItemCodeMaster.class);
		List<Predicate> predicates = new ArrayList<>();

		if (userIdStoreIdItemCode != null && !userIdStoreIdItemCode.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userIdStoreIdItemCode"), userIdStoreIdItemCode));
		}
		if (storeId != null && !storeId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("storeId"), storeId));
		}
		if (itemCode != null && !itemCode.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("itemCode"), itemCode));
		}
		if (itemName != null && !itemName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("itemName"), itemName));
		}

		if (itemCategory != null && !itemCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("itemCategory"), itemCategory));
		}
		if (itemSubCategory != null && !itemSubCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("itemSubCategory"), itemSubCategory));
		}
		if (manufacturer != null && !manufacturer.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("manufacturer"), manufacturer));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("brand"), brand));
		}
		if (hsnGroup != null && !hsnGroup.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("hsnGroup"), hsnGroup));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}
		if (userIdStoreId != null && !userIdStoreId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<ItemCodeMaster> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<ItemCodeMaster> countRoot = countQuery.from(ItemCodeMaster.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (userIdStoreIdItemCode != null && !userIdStoreIdItemCode.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userIdStoreIdItemCode"), userIdStoreIdItemCode));
		}
		if (storeId != null && !storeId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("storeId"), storeId));
		}
		if (itemCode != null && !itemCode.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("itemCode"), itemCode));
		}
		if (itemName != null && !itemName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("itemName"), itemName));
		}

		if (itemCategory != null && !itemCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("itemCategory"), itemCategory));
		}
		if (itemSubCategory != null && !itemSubCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("itemSubCategory"), itemSubCategory));
		}
		if (manufacturer != null && !manufacturer.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("manufacturer"), manufacturer));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("brand"), brand));
		}
		if (hsnGroup != null && !hsnGroup.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("hsnGroup"), hsnGroup));
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

	public List<ItemCodeSearchDTO> searchByItemCode(String itemCode) {
		return repository.searchByItemCode(itemCode);
	}

	public List<ItemCodeSearchDTO> searchByItemName(String itemName) {
		return repository.searchByItemName(itemName);
	}

	@Transactional
	public void uploadFilesAndSaveFileLink(MultipartFile image1, MultipartFile image2, MultipartFile image3,
			String userIdStoreIdItemCode) throws APIException {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);

		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
		}
		Optional<ItemCodeMaster> storeEntityOptional = repository.findByUserIdStoreIdItemCode(userIdStoreIdItemCode);
		if (!storeEntityOptional.isPresent()) {
			throw new APIException("Store Not Found for userIdStoreIdItemCode: " + userIdStoreIdItemCode);
		}
		ItemCodeMasterImage entity = new ItemCodeMasterImage();
		if (!image1.isEmpty()) {
			String uploadedURL = amazonService.uploadFile(folderName, image1);
			entity.setImage1(uploadedURL);
		}
		if (!image2.isEmpty()) {
			String uploadedURL = amazonService.uploadFile(folderName, image2);
			entity.setImage2(uploadedURL);
		}
		if (!image3.isEmpty()) {
			String uploadedURL = amazonService.uploadFile(folderName, image3);
			entity.setImage3(uploadedURL);
		}
		entity.setUserIdStoreIdItemCode(userIdStoreIdItemCode);
		entity.setUpdatedDate(new Date());
		entity.setUpdatedBy(login.get().getUserId());
		itemCodeMasterImageRepository.save(entity);
	}
//	public void uploadFilesAndSaveFileLink(MultipartFile medicinePhoto, String userIdStoreIdItemCode) throws APIException {
//
//	    Optional<ItemCodeMaster> optional = repository.findByUserIdStoreIdItemCode(userIdStoreIdItemCode);
//
//	    if (optional.isPresent()) {
//	        ItemCodeMaster entity = optional.get();
//
//	        if (!medicinePhoto.isEmpty()) {
//	            String uploadedstoreFrontURL = amazonService.uploadFile(folderName, medicinePhoto);
//	            entity.setMedicinePhoto(uploadedstoreFrontURL);
//	        }
//
//	        repository.save(entity);
//	    } else {
//	        // ✅ Option 1: Throw error (Recommended if the record MUST exist)
//	        throw new APIException("Item not found for userIdStoreIdItemCode: " + userIdStoreIdItemCode);
//	    }
//	}

}
