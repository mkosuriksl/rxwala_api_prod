package com.kosuri.stores.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosuri.stores.constant.StoreConstants;
import com.kosuri.stores.dao.AdminStoreBusinessTypeEntity;
import com.kosuri.stores.dao.AdminStoreCategoryEntity;
import com.kosuri.stores.dao.AdminStoreMembershipEntity;
import com.kosuri.stores.dao.AdminStoreMembershipRepository;
import com.kosuri.stores.dao.AdminStoreVerificationEntity;
import com.kosuri.stores.dao.AdminStoreVerificationRepository;
import com.kosuri.stores.dao.DeleteStore;
import com.kosuri.stores.dao.DeleteStoreRepository;
import com.kosuri.stores.dao.DiagnosticServiceRepository;
import com.kosuri.stores.dao.DiagnosticServicesEntity;
import com.kosuri.stores.dao.ItemCodeMaster;
import com.kosuri.stores.dao.ItemCodeMasterRepository;
import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.PurchaseHeaderEntity;
import com.kosuri.stores.dao.PurchaseHeaderRepository;
import com.kosuri.stores.dao.PurchaseRepository;
import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.SaleHeaderEntity;
import com.kosuri.stores.dao.SaleHeaderRepository;
import com.kosuri.stores.dao.SaleRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreLicenceInfoEntity;
import com.kosuri.stores.dao.StoreLicenceInfoRepository;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.StoreResponseDto;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.StoreResponseDTO;
import com.kosuri.stores.model.enums.Status;
import com.kosuri.stores.model.request.AdminStoreRequest;
import com.kosuri.stores.model.request.CreateStoreRequest;
import com.kosuri.stores.model.request.DeleteStoreIdRequest;
import com.kosuri.stores.model.request.UpdateStoreRequest;
import com.kosuri.stores.model.response.AdminStoreVerificationResponse;
import com.kosuri.stores.model.response.CreateStoreResponse;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.s3.config.AmazonS3Service;
import com.kosuri.stores.utils.CurrentUser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class StoreHandler {
	@Autowired
	private SaleHeaderRepository saleHeaderRepository;
	@Autowired
	private SaleRepository saleRepository;
	@Autowired
	private DeleteStoreRepository deleteStoreRepository;
	@Autowired
	private PurchaseHeaderRepository purchaseHeaderRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private ItemCodeMasterRepository itemCodeMasterRepository;
	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private OtpHandler otpHandler;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@Autowired
	private AmazonS3Service amazonService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AdminStoreVerificationRepository adminStoreVerificationRepository;

	@Autowired
	private StoreLicenceInfoRepository storeLicenceInfoRepository;

	private static String folderName = "store-docs/";

	@Autowired
	private DiagnosticServiceRepository diagnosticServiceRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private AdminStoreMembershipRepository adminStoreMembershipRepository;

	public String addStore(CreateStoreRequest createStoreRequest) throws Exception {
		if (validateStoreInputs(createStoreRequest)) {
			StoreEntity storeEntity = repositoryHandler
					.addStoreToRepository(createStoreEntityFromRequest(createStoreRequest));

			if (storeEntity != null) {
				otpHandler.sendOtpToEmail(storeEntity.getOwnerEmail(), false, true, false);
			}
		}
		return createStoreRequest.getUserIdStoreId();
	}

	@Transactional
	public void uploadFilesAndSaveFileLink(MultipartFile storeFrontImage, MultipartFile tradeLicense,
			MultipartFile drugLicense, String userIdstoreId) throws APIException {

		Optional<StoreEntity> storeEntityOptional = storeRepository.findByUserIdStoreId(userIdstoreId);
		if (!storeEntityOptional.isPresent()) {
			throw new APIException("Store Not Found for userIdStoreId: " + userIdstoreId);
		}

		StoreEntity storeEntity = storeEntityOptional.get();
		String userId = storeEntity.getUserId(); // Extract userId

		AdminStoreVerificationEntity entity = new AdminStoreVerificationEntity();
		if (!storeFrontImage.isEmpty()) {
			String uploadedstoreFrontURL = amazonService.uploadFile(folderName, storeFrontImage);
			entity.setDoc1(uploadedstoreFrontURL);
		}
		if (!tradeLicense.isEmpty()) {
			String uploadedtradeLicenseURL = amazonService.uploadFile(folderName, tradeLicense);
			entity.setDoc2(uploadedtradeLicenseURL);
		}

		if (!drugLicense.isEmpty()) {
			String uploadedDrugLicenseURL = amazonService.uploadFile(folderName, drugLicense);
			entity.setDoc3(uploadedDrugLicenseURL);
		}
		entity.setUserIdStoreId(userIdstoreId);
		entity.setUserId(userId);
		repositoryHandler.saveAdminStoreVerificationEntity(entity);
	}

	public void getStoreFilesByStoreId(String userIdstoreId) {
		AdminStoreVerificationEntity adminStoreVerificationEntity = repositoryHandler
				.getAdminStoreVerification(userIdstoreId);

	}

	public String updateStore(UpdateStoreRequest updateStoreRequest) throws Exception {
		if (validateUpdateStoreInputs(updateStoreRequest)) {
			StoreEntity storeEntity = repositoryHandler.updateStore(updateStoreEntityFromRequest(updateStoreRequest));

			if (storeEntity != null) {
				otpHandler.sendOtpToEmail(storeEntity.getOwnerEmail(), false, true, true);
			}
		}
		return updateStoreRequest.getId();
	}

	public List<StoreEntity> getStoreIdFromStoreOwner(String emailId) {
		Optional<List<StoreEntity>> entity = storeRepository.findByOwnerEmail(emailId);
		return entity.orElse(null);
	}

	public List<String> getStoreIdFromLocation(String location) {
		Optional<List<StoreEntity>> entity = Optional.ofNullable(storeRepository.findByLocationContaining(location));
		List<String> stores = new ArrayList<>();
		if (entity.isPresent()) {
			for (StoreEntity store : entity.get()) {
				stores.add(store.getId());
			}
		}
		return stores;
	}

	public List<StoreEntity> getAllStores() throws Exception {
		List<StoreEntity> storeEntities = repositoryHandler.getAllStores();

		List<StoreEntity> stores = new ArrayList<>();
		for (StoreEntity store : storeEntities) {
			if (store.getId() != "DUMMY") {
				stores.add(store);
			}
		}
		return stores;
	}

	private StoreEntity createStoreEntityFromRequest(CreateStoreRequest createStoreRequest) {

		LocalDate currentDate = LocalDate.now();

		StoreEntity storeEntity = new StoreEntity();
		storeEntity.setName(createStoreRequest.getName());
		storeEntity.setId(createStoreRequest.getId());
		TabStoreUserEntity tabStoreUserEntity = tabStoreRepository
				.findByStoreUserEmail(createStoreRequest.getOwnerEmail()).orElse(null);
		storeEntity.setUserId(tabStoreUserEntity.getUserId());
		storeEntity.setType(createStoreRequest.getStoreType());
		storeEntity.setPincode(createStoreRequest.getPincode());
		storeEntity.setDistrict(createStoreRequest.getDistrict());
		storeEntity.setState(createStoreRequest.getState());
		storeEntity.setOwner(createStoreRequest.getOwner());
		storeEntity.setOwnerEmail(createStoreRequest.getOwnerEmail());
		storeEntity.setOwnerContact(createStoreRequest.getOwnerContact());
		storeEntity.setSecondaryContact(createStoreRequest.getSecondaryContact());
		storeEntity.setRegistrationDate(LocalDate.now());
		storeEntity.setCreationTimeStamp(LocalDateTime.now().toString());
		storeEntity.setModifiedBy(storeEntity.getOwner());
		storeEntity.setCurrentPlan(StoreConstants.PLANID);
		storeEntity.setModifiedDate(LocalDate.now().toString());
		storeEntity.setStatus(Status.INACTIVE.name());
		storeEntity.setStoreVerifiedStatus(createStoreRequest.getStoreVerificationStatus());
		storeEntity.setModifiedTimeStamp(LocalDateTime.now().toString());
		try {
			setExpirationDateForStore(storeEntity, createStoreRequest.getStoreType());
		} catch (DateTimeParseException e) {
			storeEntity.setStatus("Inactive");
			storeEntity.setExpiryDate(null);
		}
		storeEntity.setAddedBy(createStoreRequest.getOwner());
		storeEntity.setLocation(createStoreRequest.getLocation());
		storeEntity.setStoreBusinessType(getBusinessStoreType(createStoreRequest.getBusinessType()));
		storeEntity.setStoreVerifiedStatus(createStoreRequest.getStoreVerificationStatus());
		storeEntity.setGstNumber(createStoreRequest.getGstNumber());
		return storeEntity;
	}

	private String getBusinessStoreType(String businessType) {
		AdminStoreBusinessTypeEntity adminStoreBusinessType = repositoryHandler
				.getStoreBusinessTypeByName(businessType);
		return adminStoreBusinessType.getBusinessTypeId();
	}

	private void setExpirationDateForStore(StoreEntity storeEntity, String storeCategory) {
		AdminStoreMembershipEntity adminStoreMembershipEntity = repositoryHandler
				.getStoreVerificationDetails(storeCategory);
		if (adminStoreMembershipEntity != null && storeEntity.getRegistrationDate() != null) {
			int noOfDays = Integer.parseInt(adminStoreMembershipEntity.getNoOfDays());
			LocalDate registrationDate = storeEntity.getRegistrationDate();
			LocalDate expirationDate = registrationDate.plusDays(noOfDays);
			storeEntity.setExpiryDate(expirationDate.toString());
		}
	}

	private StoreEntity updateStoreEntityFromRequest(UpdateStoreRequest request) {
		StoreEntity storeEntity = storeRepository.findById(request.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Store Id Not Found By : " + request.getId()));
		storeEntity.setName(request.getName());
		storeEntity.setId(request.getId());
		storeEntity.setType(request.getStoreType());
		storeEntity.setPincode(request.getPincode());
		storeEntity.setPincode(request.getPincode());
		storeEntity.setDistrict(request.getDistrict());
		storeEntity.setState(request.getState());
		storeEntity.setOwner(request.getOwner());
		storeEntity.setOwnerEmail(request.getOwnerEmail());
		storeEntity.setOwnerContact(request.getOwnerContact());
		storeEntity.setSecondaryContact(request.getSecondaryContact());
		storeEntity.setRegistrationDate(LocalDate.now());
		storeEntity.setCreationTimeStamp(LocalDateTime.now().toString());
		storeEntity.setRole(StoreConstants.STORE_MANAGHER);
		storeEntity.setModifiedBy(request.getOwner());
		storeEntity.setModifiedDate(LocalDate.now().toString());
		storeEntity.setModifiedTimeStamp(LocalDateTime.now().toString());
		storeEntity.setStatus(request.getStatus());
		storeEntity.setAddedBy(request.getOwner());
		storeEntity.setLocation(request.getLocation());

		return storeEntity;
	}

	boolean validateStoreInputs(CreateStoreRequest request) throws Exception {
//		boolean isStorePresent = repositoryHandler.isStorePresent(request);
//		if (isStorePresent) {
//			throw new APIException("Store Is Already Present In System");
//		}

		if (request.getOwnerEmail() != null && !request.getOwnerEmail().isEmpty() && request.getOwnerContact() != null
				&& !request.getOwnerContact().isEmpty() && request.getExpirationDate() != null
				&& !request.getExpirationDate().isEmpty()) {
			boolean isOwnerPresent = repositoryHandler.isOwnerPresent(request.getOwnerEmail(),
					request.getOwnerContact());
			if (!isOwnerPresent) {
				throw new APIException("Owner Not Found");
			}
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String expirationDateString = request.getExpirationDate();
			LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);
			if (!expirationDate.isAfter(currentDate)) {
				throw new APIException("Cannot Add Store As Store Licence Expired.");
			}

		}
		return true;
	}

	boolean validateUpdateStoreInputs(UpdateStoreRequest request) throws Exception {
		Optional<StoreEntity> store = storeRepository.findById(request.getId());
		if (!store.isPresent()) {
			throw new APIException("Store with id not found");
		}

		if (request.getOwnerEmail() != null && !request.getOwnerEmail().isEmpty() && request.getOwnerContact() != null
				&& !request.getOwnerContact().isEmpty()) {
			TabStoreUserEntity storeUser = tabStoreRepository
					.findByStoreUserEmailAndStoreUserContact(request.getOwnerEmail(), request.getOwnerContact());
			if (storeUser != null) {
				return true;
			} else {
				throw new APIException("User Is not registered");
			}

		}
		return true;
	}

	public AdminStoreVerificationResponse downloadStoreDocs(String storeId) {
		AdminStoreVerificationEntity adminStoreVerification = adminStoreVerificationRepository
				.findByUserIdStoreId(storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Store Not Found By : " + storeId));
		return modelMapper.map(adminStoreVerification, AdminStoreVerificationResponse.class);
	}

//	public List<StoreEntity> searchStores(String location, String userId, String storeType, LocalDate addedDate,
//			String mobile, String email, LocalDate toRegDate, LocalDate fromRegDate) throws APIException {
//
//		List<StoreEntity> stores = new ArrayList<>();
//
//		if (userId != null && !userId.isEmpty()) {
//			Optional<TabStoreUserEntity> storeUser = tabStoreRepository.findById(userId);
//
//			/*
//			 * if (storeUser.isPresent()) {
//			 * 
//			 * String ownerEmail = storeUser.get().getStoreUserEmail(); if (storeType !=
//			 * null && !storeType.isEmpty()) { stores =
//			 * storeRepository.findByOwnerEmailAndType(ownerEmail, storeType); } else {
//			 * stores = storeRepository.findByOwnerEmail(ownerEmail).orElse(new
//			 * ArrayList<>()); } } } else if (location != null && !location.isEmpty()) { if
//			 * (storeType != null && !storeType.isEmpty()) { stores =
//			 * storeRepository.findByLocationAndType(location, storeType); } else { stores =
//			 * storeRepository.findByLocationContaining(location); } } else if (addedDate !=
//			 * null && addedDate != null) {
//			 * 
//			 * stores = storeRepository.findByRegistrationDate(addedDate); } else if (mobile
//			 * != null || email != null) { stores =
//			 * storeRepository.findByOwnerEmailOrOwnerContact(email, mobile).get(); } else
//			 * if (mobile != null && email != null) { stores =
//			 * storeRepository.findByOwnerEmailAndOwnerContact(email, mobile);
//			 * 
//			 * } else if (toRegDate != null && fromRegDate != null) { stores =
//			 * storeRepository.findAllBetweenRegistrationDate(toRegDate, fromRegDate); }
//			 */
//
//			stores = storeRepository.findAllByData(storeUser.get().getUserId(), location, storeType, addedDate, mobile,
//					email, toRegDate, fromRegDate);
//		} else {
//			throw new APIException("No stores found.");
//		}
//		return stores;
//	}

//public List<StoreEntity> searchStores(String location, String userId, String storeType, LocalDate addedDate,
//		String mobile, String email, LocalDate toRegDate, LocalDate fromRegDate,String userIdStoreId,Map<String, String> requestParams) {
//		
//		List<String> expectedParams = Arrays.asList("location","userId","storeType","addedDate","mobile","email","toRegDate","fromRegDate","userIdStoreId");
//	    for (String paramName : requestParams.keySet()) {
//	        if (!expectedParams.contains(paramName)) {
//	            throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
//	        }
//	    }
//	    
//		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//		CriteriaQuery<StoreEntity> query = cb.createQuery(StoreEntity.class);
//		Root<StoreEntity> root = query.from(StoreEntity.class);
//		List<Predicate> predicates = new ArrayList<>();
//		
//		if (location != null) {
//			predicates.add(cb.equal(root.get("location"), location));
//		}
//		if (userId != null) {
//			predicates.add(cb.equal(root.get("userId"), userId));
//		}
//		if (userIdStoreId != null) {
//			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
//		}
//		if (storeType != null) {
//			predicates.add(cb.equal(root.get("type"), storeType));
//		}
//		if (mobile != null) {
//			predicates.add(cb.equal(root.get("ownerContact"), mobile));
//		}
//		if (email != null) {
//			predicates.add(cb.equal(root.get("ownerEmail"), email));
//		}
//		if (fromRegDate != null && toRegDate != null) {
//			predicates.add(cb.between(root.get("registrationDate"), fromRegDate, toRegDate));
//		} else if (fromRegDate != null) {
//			predicates.add(cb.equal(root.get("registrationDate"), fromRegDate));
//		}
//		if (addedDate != null) {
//			predicates.add(cb.equal(root.get("registrationDate"), addedDate));
//		}
//		
//		query.where(predicates.toArray(new Predicate[0]));
//
//		return entityManager.createQuery(query).getResultList();
//	}

	public Page<StoreEntity> searchStores(String location, String userId, String storeType, LocalDate addedDate,
			String mobile, String email, LocalDate toRegDate, LocalDate fromRegDate, String userIdStoreId,
			Map<String, String> requestParams, Pageable pageable) {

		List<String> expectedParams = Arrays.asList("location", "userId", "storeType", "addedDate", "mobile", "email",
				"toRegDate", "fromRegDate", "userIdStoreId");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<StoreEntity> query = cb.createQuery(StoreEntity.class);
		Root<StoreEntity> root = query.from(StoreEntity.class);
		List<Predicate> predicates = new ArrayList<>();

		if (location != null) {
			predicates.add(cb.equal(root.get("location"), location));
		}
		if (userId != null) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}
		if (userIdStoreId != null) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}
		if (storeType != null) {
			predicates.add(cb.equal(root.get("type"), storeType));
		}
		if (mobile != null) {
			predicates.add(cb.equal(root.get("ownerContact"), mobile));
		}
		if (email != null) {
			predicates.add(cb.equal(root.get("ownerEmail"), email));
		}
		if (fromRegDate != null && toRegDate != null) {
			predicates.add(cb.between(root.get("registrationDate"), fromRegDate, toRegDate));
		} else if (fromRegDate != null) {
			predicates.add(cb.equal(root.get("registrationDate"), fromRegDate));
		}
		if (addedDate != null) {
			predicates.add(cb.equal(root.get("registrationDate"), addedDate));
		}

		query.where(predicates.toArray(new Predicate[0]));

//			return entityManager.createQuery(query).getResultList();
		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<StoreEntity> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<StoreEntity> countRoot = countQuery.from(StoreEntity.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (location != null) {
			countPredicates.add(cb.equal(countRoot.get("location"), location));
		}
		if (userId != null) {
			countPredicates.add(cb.equal(countRoot.get("userId"), userId));
		}
		if (userIdStoreId != null) {
			countPredicates.add(cb.equal(countRoot.get("userIdStoreId"), userIdStoreId));
		}
		if (storeType != null) {
			countPredicates.add(cb.equal(countRoot.get("type"), storeType));
		}
		if (mobile != null) {
			countPredicates.add(cb.equal(countRoot.get("ownerContact"), mobile));
		}
		if (email != null) {
			countPredicates.add(cb.equal(countRoot.get("ownerEmail"), email));
		}
		if (fromRegDate != null && toRegDate != null) {
			countPredicates.add(cb.between(countRoot.get("registrationDate"), fromRegDate, toRegDate));
		} else if (fromRegDate != null) {
			countPredicates.add(cb.equal(countRoot.get("registrationDate"), fromRegDate));
		}
		if (addedDate != null) {
			countPredicates.add(cb.equal(countRoot.get("registrationDate"), addedDate));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	public CreateStoreResponse updateStoreDocumentVerification(AdminStoreRequest adminStoreRequest)
			throws APIException {
		CreateStoreResponse response = new CreateStoreResponse();
		String loggedInUserEmail = CurrentUser.getEmail();
		TabStoreUserEntity user = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail)
				.orElseThrow(() -> new RuntimeException("Store User Found"));
		if (user.getUserType().equals("AU")) {
			AdminStoreVerificationEntity storeVerificationEntity = repositoryHandler
					.getAdminStoreVerification(adminStoreRequest.getUserIdstoreId());
			if (storeVerificationEntity != null) {
				if ("Verified".equals(storeVerificationEntity.getVerificationStatus())) {
					throw new RuntimeException(
							"Store with ID: " + storeVerificationEntity.getUserIdStoreId() + " is already verified.");
				}
				storeVerificationEntity
						.setVerificationStatus(adminStoreRequest.getIsStoreValid() ? "Verified" : "Rejected");
				storeVerificationEntity.setComment(adminStoreRequest.getComments());
				storeVerificationEntity.setVerifiedBy(adminStoreRequest.getVerifiedBy());
				storeVerificationEntity.setVerificationDate(LocalDateTime.now());
				AdminStoreVerificationEntity adminstoreVerification = repositoryHandler
						.saveAdminStoreVerificationEntity(storeVerificationEntity);
				if (adminstoreVerification != null) {
					StoreEntity storeEntity = storeRepository.findByUserIdStoreId(adminStoreRequest.getUserIdstoreId())
							.orElseThrow(() -> new RuntimeException("Store Not Found"));

					// Update status and verification flag
					storeEntity.setStatus("ACTIVE");
					storeEntity.setStoreVerifiedStatus("true");

					// ✅ Fetch storeCategory
					String storeCategory = storeEntity.getType(); // or storeVerificationEntity.getStoreCategory()

					String planId = storeEntity.getCurrentPlan();
					// ✅ Get membership info using category
					Optional<AdminStoreMembershipEntity> adminStoreMembershipEntity = adminStoreMembershipRepository
							.findByPlanIdAndStoreCategory(planId, storeCategory);

					// ✅ Set expiry date if membership found
					if (adminStoreMembershipEntity.isPresent() && storeEntity.getRegistrationDate() != null) {
						int noOfDays = Integer.parseInt(adminStoreMembershipEntity.get().getNoOfDays());
						LocalDate expiryDate = storeEntity.getRegistrationDate().plusDays(noOfDays);
						storeEntity.setExpiryDate(expiryDate.toString());
					}

					storeRepository.save(storeEntity);
					StoreLicenceInfoEntity storeLicenceInfo = storeLicenceInfoRepository
							.findByStoreId(adminStoreRequest.getUserIdstoreId());
					String gst = null;
					// ******** Sending Email ********
					if (storeLicenceInfo != null) {
						gst = (storeLicenceInfo.getGstNumber() != null) ? storeLicenceInfo.getGstNumber() : null;
					}

					// Email subject
					String subject = "Your Store is Successfully Verified!";

					// Email Message
					String message = "Your store verification is complete. You can now manage your store.\n"
							+ "Store ID: " + adminstoreVerification.getUserIdStoreId() + "\n" + "Store Owner: "
							+ storeEntity.getName() + "\n" + "Store Location: " + storeEntity.getLocation() + "\n"
							+ "Store Pincode: " + storeEntity.getPincode() + "\n" + "GST ID: " + gst;
					emailService.sendEmailMessage(user.getStoreAdminEmail(), message, subject);
					response.setId(storeEntity.getId());
					response.setUserIdStoreId(storeVerificationEntity.getUserIdStoreId());
					response.setResponseMessage("Store documents Verification successfully for StoreId: "
							+ adminStoreRequest.getUserIdstoreId());
					return response;
				}
			} else {
				throw new APIException("Store with ID: " + adminStoreRequest.getUserIdstoreId() + " not found.");
			}
		} else {
			response.setResponseMessage("You don't have authorization to access this API.");
		}
		return response;
	}

	public List<AdminStoreBusinessTypeEntity> getAllStoreBusinessTypes() throws APIException {
		return repositoryHandler.getAllAdminStoreBusinessTypes();
	}

	public List<AdminStoreCategoryEntity> getAllStoreCategories() throws APIException {
		return repositoryHandler.getAllAdminStoreCategories();
	}

	public List<AdminStoreMembershipEntity> getAllAdminStoreMembership() {
		return repositoryHandler.getAllAdminStoreMembership();
	}

	public List<String> getDistinctLocationsByStoreCategory(String storeCategory, Map<String, String> requestParams) {
		List<String> expectedParams = Arrays.asList("storeCategory");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		return storeRepository.findDistinctLocationsByType(storeCategory);
	}

	public List<String> getDistinctLocationsByStoreCategoryEn(String storeCategory, String storeBusinessType) {
		if (storeBusinessType != null && !storeBusinessType.isBlank()) {
			return storeRepository.findDistinctLocationsByTypeAndBusinessType(storeCategory, storeBusinessType);
		} else {
			return storeRepository.findDistinctLocationsByType(storeCategory);
		}
	}

	public List<String> getDistinctLocationsByStoreCategoryEn() {
		String storeCategory = "pharmacy";
		String storeBusinessType = "DT";

		return storeRepository.findDistinctLocationsByTypeAndBusinessType(storeCategory, storeBusinessType);
	}

	public List<String> getServiceCategoriesByStoreCategory(String storeCategory) {
		List<StoreEntity> stores = storeRepository.findByTypes(storeCategory);

		if (stores.isEmpty()) {
			throw new RuntimeException("No stores found for category: " + storeCategory);
		}

		List<String> storeIds = stores.stream().map(StoreEntity::getId).collect(Collectors.toList());

		List<DiagnosticServicesEntity> diagnosticServices = diagnosticServiceRepository.findByStoreIdIn(storeIds);

		return diagnosticServices.stream().map(DiagnosticServicesEntity::getServiceCategory).distinct()
				.collect(Collectors.toList());
	}

	public List<StoreEntity> getStoresByLocationAndBusinessType(String location, String storeBusinessType) {
		return storeRepository.findByLocationAndStoreBusinessType(location, storeBusinessType);
	}

	public List<StoreResponseDTO> getStoresByLocationAndBusinessTypeEn(String location, String storeBusinessType) {
		List<StoreEntity> storeEntities = storeRepository.findByLocationAndStoreBusinessType(location,
				storeBusinessType);

		return storeEntities.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private StoreResponseDTO convertToDTO(StoreEntity store) {
		StoreResponseDTO dto = new StoreResponseDTO();
		dto.setType(store.getType());
		dto.setUserIdStoreId(store.getUserIdStoreId());
		dto.setId(store.getId());
		dto.setName(store.getName());
		dto.setPincode(store.getPincode());
		dto.setDistrict(store.getDistrict());
		dto.setState(store.getState());
		dto.setLocation(store.getLocation());
		dto.setOwner(store.getOwner());
		dto.setOwnerContact(store.getOwnerContact());
		dto.setSecondaryContact(store.getSecondaryContact());
		dto.setOwnerEmail(store.getOwnerEmail());
		dto.setStoreBusinessType(store.getStoreBusinessType());
		dto.setUserId(store.getUserId());
		return dto;
	}

	public List<TabStoreUserEntity> getStoreTabUser(String userId, String userIdstoreId, String storeUserContact,
			String storeUserEmail) {
		if (userId != null && storeUserContact != null) {
			return tabStoreRepository.findByUserIdAndStoreUserContact(userId, storeUserContact);
		} else if (userId != null && storeUserEmail != null) {
			return tabStoreRepository.findByUserIdAndStoreUserEmail(userId, storeUserEmail);
		} else if (userId != null) {
			return tabStoreRepository.findByUserIds(userId);
		} else if (storeUserContact != null) {
			return tabStoreRepository.findByStoreUserContacts(storeUserContact);
		} else if (storeUserEmail != null) {
			return tabStoreRepository.findByStoreUserEmails(storeUserEmail);
		} else {
			return tabStoreRepository.findAll(); // Return all if no filter
		}
	}

	public List<StoreEntity> getStoreInfoByStoreId(String storeId) {
		return storeRepository.findByStoreId(storeId);
	}

	public List<String> getDistinctStoreBusinessTypes() {
		return storeRepository.findDistinctStoreBusinessTypes();
	}

	public List<String> getDistinctStoreBusinessTypesByLocation(String location) {
		return storeRepository.findDistinctStoreBusinessTypesByLocation(location);
	}

	public Map<String, Object> getlocationBySearch(String location) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
		Root<StoreEntity> root = query.from(StoreEntity.class);

		List<Predicate> predicates = new ArrayList<>();
		if (location != null && !location.isBlank()) {
			predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
		}

		query.multiselect(root.get("location"));
		query.where(cb.and(predicates.toArray(new Predicate[0])));

		List<Object[]> resultList = entityManager.createQuery(query).getResultList();

		List<Map<String, Object>> responseData = resultList.stream().map(obj -> {
			Map<String, Object> map = new HashMap<>();
			map.put("location", obj[0]);
			return map;
		}).collect(Collectors.toList());

		return Map.of("message", "location fetched successfully", "status", true, "totalCount", responseData.size(), // ✅
																														// Total
																														// count
																														// added
				"data", responseData);
	}

	@Transactional
	public GenericResponse storeIdDelete(DeleteStoreIdRequest storeIdRequest) {
	    GenericResponse response = new GenericResponse();

	    try {
	        StoreEntity entity = storeRepository.findById(storeIdRequest.getStoreId())
	                .orElseThrow(() -> new APIException("StoreId Not Found By: " + storeIdRequest.getStoreId()));

	        // Check if any dependent data exists
	        boolean hasItemCode = itemCodeMasterRepository.findByStoreId(entity.getId()).isPresent()
	                && !itemCodeMasterRepository.findByStoreId(entity.getId()).get().isEmpty();

	        boolean hasPurchase = purchaseRepository.findByStoreId(entity.getId()).isPresent()
	                && !purchaseRepository.findByStoreId(entity.getId()).get().isEmpty();

	        boolean hasPurchaseHeader = purchaseHeaderRepository.findByStoreId(entity.getId()).isPresent()
	                && !purchaseHeaderRepository.findByStoreId(entity.getId()).get().isEmpty();

	        boolean hasSale = saleRepository.findByStoreId(entity.getId()).isPresent()
	                && !saleRepository.findByStoreId(entity.getId()).get().isEmpty();

	        boolean hasSaleHeader = saleHeaderRepository.findByStoreId(entity.getId()).isPresent()
	                && !saleHeaderRepository.findByStoreId(entity.getId()).get().isEmpty();

	        // If any related data exists, prevent deletion
	        if (hasItemCode || hasPurchase || hasPurchaseHeader || hasSale || hasSaleHeader) {
	            response.setResponseMessage("Cannot delete store. It is associated with existing records.");
	            response.setStatus(false);
	            return response;
	        }

	        // Only delete store if no related data exists
	        storeRepository.delete(entity); // This deletes the store

	        // Save deletion record to audit table
	        DeleteStore deleteUser = new DeleteStore();
	        deleteUser.setDeletedDate(LocalDateTime.now());
	        deleteUser.setDeleteReason(storeIdRequest.getReason());
	        deleteUser.setEmail(entity.getOwnerEmail());
	        deleteUser.setPhone(entity.getOwnerContact());
	        deleteUser.setDeactivated(true);
	        deleteUser.setCandidateId(entity.getUserId());
	        deleteUser.setStoreId(entity.getId());
	        deleteStoreRepository.save(deleteUser);

	        response.setResponseMessage("Store deleted successfully.");
	        response.setStatus(true);
	        return response;

	    } catch (APIException e) {
	        e.printStackTrace();
	        response.setResponseMessage("Error: " + e.getMessage());
	        response.setStatus(false);
	        return response;
	    }
	}
	
	 public List<StoreResponseDto> getPCStoresByName(String name) {
	        List<StoreEntity> stores = storeRepository.findByNameContainingIgnoreCaseAndType(name, "PC");
	        return stores.stream()
	                .map(store -> new StoreResponseDto(
	                        store.getLocation(),
	                        store.getId(),
	                        store.getOwnerContact(),
	                        store.getType(),
	                        store.getName()
	                ))
	                .collect(Collectors.toList());
	    }
	 
	 public List<StoreResponseDto> getDCStoresByName(String name) {
	        List<StoreEntity> stores = storeRepository.findByNameContainingIgnoreCaseAndType(name, "DC");
	        return stores.stream()
	                .map(store -> new StoreResponseDto(
	                        store.getLocation(),
	                        store.getId(),
	                        store.getOwnerContact(),
	                        store.getType(),
	                        store.getName()
	                ))
	                .collect(Collectors.toList());
	    }
	 
	 public List<StoreResponseDto> getPHStoresByName(String name) {
	        List<StoreEntity> stores = storeRepository.findByNameContainingIgnoreCaseAndType(name, "PH");
	        return stores.stream()
	                .map(store -> new StoreResponseDto(
	                        store.getLocation(),
	                        store.getId(),
	                        store.getOwnerContact(),
	                        store.getType(),
	                        store.getName()
	                ))
	                .collect(Collectors.toList());
	    }
}
