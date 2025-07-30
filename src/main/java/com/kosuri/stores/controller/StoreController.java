package com.kosuri.stores.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosuri.stores.dao.AdminStoreBusinessTypeEntity;
import com.kosuri.stores.dao.AdminStoreCategoryEntity;
import com.kosuri.stores.dao.AdminStoreMembershipEntity;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreResponseDto;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.AdminStoreMembershipHandler;
import com.kosuri.stores.handler.MembershipHdrService;
import com.kosuri.stores.handler.RepositoryHandler;
import com.kosuri.stores.handler.StoreHandler;
import com.kosuri.stores.model.dto.StoreResponseDTO;
import com.kosuri.stores.model.request.AdminStoreRequest;
import com.kosuri.stores.model.request.CreateAdminStoreMembershipRequest;
import com.kosuri.stores.model.request.CreateStoreRequest;
import com.kosuri.stores.model.request.DeleteAccountRequest;
import com.kosuri.stores.model.request.DeleteStoreIdRequest;
import com.kosuri.stores.model.request.RenewMembershipVerificationRequest;
import com.kosuri.stores.model.request.RenewStoreMembershipRequest;
import com.kosuri.stores.model.request.UpdateAdminStoreMembershipRequest;
import com.kosuri.stores.model.request.UpdateStoreRequest;
import com.kosuri.stores.model.response.AdminStoreVerificationResponse;
import com.kosuri.stores.model.response.CreateAdminStoreMembershipResponse;
import com.kosuri.stores.model.response.CreateRenewMemberStoreResponse;
import com.kosuri.stores.model.response.CreateStoreResponse;
import com.kosuri.stores.model.response.CreateStoresResponse;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.GetStoreMembershipResponse;
import com.kosuri.stores.model.response.GetStoreRelatedResponse;
import com.kosuri.stores.model.response.RenewStoreMembershipResponse;
import com.kosuri.stores.model.response.RenewalStoreMemberships;
import com.kosuri.stores.model.response.ResponseGetStoreRelatedResponse;
import com.kosuri.stores.model.response.UpdateStoreResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/store")
public class StoreController {
	@Autowired
	private StoreHandler storeHandler;

	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private AdminStoreMembershipHandler adminStoreMembershipService;

	@Autowired
	private MembershipHdrService membershipHdrService;

	@PostMapping("/create")
	ResponseEntity<CreateStoresResponse> createStore(@RequestBody CreateStoreRequest request) {
		CreateStoresResponse createStoreResponse = new CreateStoresResponse();
		HttpStatus httpStatus;
		try {
			createStoreResponse.setUserIdStoreId(storeHandler.addStore(request));
			createStoreResponse.setResponseMessage(
					"Store Added successfully and Email notification has been send to registered store email Id!");
			httpStatus = HttpStatus.OK;
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			createStoreResponse.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			createStoreResponse.setResponseMessage(e.getMessage());
		}
		return ResponseEntity.status(httpStatus).body(createStoreResponse);
	}

	@PostMapping("/upload")
	ResponseEntity<CreateStoreResponse> uploadStoreDocs(@RequestParam("userIdStoreId") String userIdStoreId,
			@RequestParam("storeFrontImage") MultipartFile storeFrontImage,
			@RequestParam("tradeLicense") MultipartFile tradeLicense,
			@RequestParam("drugLicense") MultipartFile drugLicense) {
		CreateStoreResponse createStoreResponse = new CreateStoreResponse();

		try {
			if (repositoryHandler.isStorePresent(userIdStoreId)) {

				storeHandler.uploadFilesAndSaveFileLink(storeFrontImage, tradeLicense, drugLicense, userIdStoreId);
				createStoreResponse.setResponseMessage("Store documents uploaded successfully for StoreId: " + userIdStoreId);
			} else {
				throw new APIException("Store Not Present");
			}

			return ResponseEntity.ok(createStoreResponse);
		} catch (APIException e) {
			createStoreResponse.setResponseMessage("Error for StoreId " + userIdStoreId + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createStoreResponse);
		} catch (Exception e) {
			createStoreResponse.setResponseMessage("Internal error for StoreId " + userIdStoreId + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createStoreResponse);
		}
	}

	@GetMapping("/downloadFiles")
	private ResponseEntity<AdminStoreVerificationResponse> downloadStoreDocs(@RequestParam("storeId") String storeId) {
		return ResponseEntity.ok(storeHandler.downloadStoreDocs(storeId));
	}

	@PutMapping("/updateStoreDocVerification")
	ResponseEntity<CreateStoreResponse> updateStoreVerification(@RequestBody AdminStoreRequest adminStoreRequest) {
		CreateStoreResponse createStoreResponse = new CreateStoreResponse();
		try {
			if (repositoryHandler.isStorePresent(adminStoreRequest.getUserIdstoreId())) {
				createStoreResponse = storeHandler.updateStoreDocumentVerification(adminStoreRequest);
			} else {
				throw new APIException("Store Not Present");
			}

			return ResponseEntity.ok(createStoreResponse);
		} catch (APIException e) {
			createStoreResponse
					.setResponseMessage("Error for StoreId " + adminStoreRequest.getUserIdstoreId() + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createStoreResponse);
		} catch (Exception e) {
			createStoreResponse.setResponseMessage(
					"StoreId " + adminStoreRequest.getUserIdstoreId() + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createStoreResponse);
		}
	}

	@PutMapping("/update")
	ResponseEntity<UpdateStoreResponse> updateStore(@RequestBody UpdateStoreRequest request) {
		HttpStatus httpStatus;
		UpdateStoreResponse updateStoreResponse = new UpdateStoreResponse();

		try {
			String storeId = storeHandler.updateStore(request);
			httpStatus = HttpStatus.OK;
			updateStoreResponse.setId(storeId);
			updateStoreResponse.setMessage("Store Details Updated SuccessFully");
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			updateStoreResponse.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			updateStoreResponse.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(updateStoreResponse);
	}

	@GetMapping("/all")
	ResponseEntity<GetStoreRelatedResponse> getAllStores() {
		HttpStatus httpStatus;
		GetStoreRelatedResponse getStoreRelatedResponse = new GetStoreRelatedResponse();

		try {
			List<StoreEntity> stores = storeHandler.getAllStores();
			getStoreRelatedResponse.setStores(stores);
			httpStatus = HttpStatus.OK;
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			getStoreRelatedResponse.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			getStoreRelatedResponse.setResponseMessage(e.getMessage());
		}
		return ResponseEntity.status(httpStatus).body(getStoreRelatedResponse);
	}

	// mobile number and email and between registration
	@GetMapping("/storeDetails")
	ResponseEntity<ResponseGetStoreRelatedResponse> getAllStoresByUserId(
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "storeType", required = false) String storeType,
			@RequestParam(required = false) String mobile, @RequestParam(required = false) String email,
			@RequestParam(required = false) LocalDate toRegDate, @RequestParam(required = false) LocalDate fromRegDate,
			@RequestParam(value = "added_date", required = false) LocalDate addedDate,@RequestParam(required = false) String userIdStoreId,@RequestParam(required = false) Map<String, String> requestParams,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws APIException {
		Pageable pageable = PageRequest.of(page, size);
		Page<StoreEntity> srpqPage = storeHandler.searchStores(location,
				userId, storeType,addedDate, mobile,email,toRegDate,fromRegDate,userIdStoreId,requestParams, pageable);
		ResponseGetStoreRelatedResponse response = new ResponseGetStoreRelatedResponse();

		response.setMessage("Store  details retrieved successfully.");
		response.setStatus(true);
		response.setStores(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
//	ResponseEntity<GetStoreRelatedResponse> getAllStoresByUserId(
//			@RequestParam(value = "location", required = false) String location,
//			@RequestParam(value = "userId", required = false) String userId,
//			@RequestParam(value = "storeType", required = false) String storeType,
//			@RequestParam(required = false) String mobile, @RequestParam(required = false) String email,
//			@RequestParam(required = false) LocalDate toRegDate, @RequestParam(required = false) LocalDate fromRegDate,
//			@RequestParam(value = "added_date", required = false) LocalDate addedDate,@RequestParam(required = false) String userIdStoreId,@RequestParam(required = false) Map<String, String> requestParams) throws APIException {
//		HttpStatus httpStatus;
//		GetStoreRelatedResponse getStoreRelatedResponse = new GetStoreRelatedResponse();
//
//		try {
//			List<StoreEntity> stores = storeHandler.searchStores(location, userId, storeType, addedDate, mobile, email,
//					toRegDate, fromRegDate,userIdStoreId,requestParams);
//			getStoreRelatedResponse.setStores(stores);
//			httpStatus = HttpStatus.OK;
//		} catch (Exception e) {
//			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//			getStoreRelatedResponse.setResponseMessage(e.getMessage());
//		}
//		return ResponseEntity.status(httpStatus).body(getStoreRelatedResponse);
//	}

	@GetMapping("/getAllStoreBusinessTypes")
	ResponseEntity<GetStoreRelatedResponse> getAllStoresBusinessTypes() throws APIException {
		HttpStatus httpStatus;
		GetStoreRelatedResponse getStoreRelatedResponse = new GetStoreRelatedResponse();
		try {
			List<AdminStoreBusinessTypeEntity> storeBusinessTypeList = storeHandler.getAllStoreBusinessTypes();
			getStoreRelatedResponse.setStoreBusinessTypeList(storeBusinessTypeList);
			httpStatus = HttpStatus.OK;
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			getStoreRelatedResponse.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			getStoreRelatedResponse.setResponseMessage(e.getMessage());
		}
		return ResponseEntity.status(httpStatus).body(getStoreRelatedResponse);
	}

	@GetMapping("/getAllStoreCategories")
	ResponseEntity<GetStoreRelatedResponse> getAllStoresCategories() throws APIException {
		HttpStatus httpStatus;
		GetStoreRelatedResponse getStoreRelatedResponse = new GetStoreRelatedResponse();
		try {
			List<AdminStoreCategoryEntity> storeCategoriesList = storeHandler.getAllStoreCategories();
			getStoreRelatedResponse.setStoreCategoriesList(storeCategoriesList);
			httpStatus = HttpStatus.OK;
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			getStoreRelatedResponse.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			getStoreRelatedResponse.setResponseMessage(e.getMessage());
		}
		return ResponseEntity.status(httpStatus).body(getStoreRelatedResponse);
	}

	@GetMapping("/getAllAdminMembership")
	ResponseEntity<GetStoreMembershipResponse> getAllAdminMembership(
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "planId", required = false) String planId,
			@RequestParam(value = "noOfDays", required = false) String noOfDays) {
		GetStoreMembershipResponse getStoreMembershipResponse = new GetStoreMembershipResponse();
		List<AdminStoreMembershipEntity> storeMembershipList = adminStoreMembershipService
				.getAllAdminStoreMembership(category, planId, noOfDays);
		getStoreMembershipResponse.setStoreMembershipList(storeMembershipList);
		return ResponseEntity.status(HttpStatus.OK).body(getStoreMembershipResponse);
	}

	@PostMapping("/addAdminStoreMembership")
	ResponseEntity<CreateAdminStoreMembershipResponse> createAdminStoreMembership(
			@Valid @RequestBody CreateAdminStoreMembershipRequest request) throws Exception {
		CreateAdminStoreMembershipResponse createAdminStoreMembershipResponse = new CreateAdminStoreMembershipResponse();
		createAdminStoreMembershipResponse.setId(adminStoreMembershipService.createAdminStoreMembership(request));
		createAdminStoreMembershipResponse.setResponseMessage("Admin Store Membership Added successfully!");
		return ResponseEntity.status(HttpStatus.OK).body(createAdminStoreMembershipResponse);
	}

	@PatchMapping("/updateAdminStoreMembership")
	ResponseEntity<UpdateStoreResponse> updateAdminStoreMembership(
			@Valid @RequestBody UpdateAdminStoreMembershipRequest request) throws Exception {
		UpdateStoreResponse updateStoreResponse = new UpdateStoreResponse();
		updateStoreResponse.setId(adminStoreMembershipService.updateAdminStoreMembership(request));
		return ResponseEntity.status(HttpStatus.OK).body(updateStoreResponse);
	}

	@PostMapping("/addRenewStoreMembershipRequest")
	public ResponseEntity<CreateRenewMemberStoreResponse> renewStoreMembership(
            @RequestBody List<RenewStoreMembershipRequest> requestList) {
        CreateRenewMemberStoreResponse response = membershipHdrService.addRenewStoreMembershipReq(requestList);
        return ResponseEntity.ok(response);
    }
//	ResponseEntity<CreateRenewMemberStoreResponse> createAdminStoreMembership(
//			@Valid @RequestBody List<RenewStoreMembershipRequest> requestList) {
//		CreateRenewMemberStoreResponse renewStoreMembershipRes = new CreateRenewMemberStoreResponse();
//		renewStoreMembershipRes.setOrderId(membershipHdrService.addRenewStoreMembershipReq(requestList));
//		renewStoreMembershipRes.setResponseMessage("Store Membership Renewed Request Added successfully!");
//		return ResponseEntity.status(HttpStatus.OK).body(renewStoreMembershipRes);
//	}

	@PostMapping("/verifyRenewStoreMembershipRequest")
	ResponseEntity<CreateStoreResponse> verifyAdminStoreMembership(
			@Valid @RequestBody RenewMembershipVerificationRequest request) {
		CreateStoreResponse renewStoreMembershipRes = new CreateStoreResponse();
		renewStoreMembershipRes.setId(membershipHdrService.verifyRenewStoreMembershipReq(request));
		renewStoreMembershipRes.setResponseMessage("Store Membership Renewed Request Verified successfully!");
		return ResponseEntity.status(HttpStatus.OK).body(renewStoreMembershipRes);
	}

	@GetMapping("/getAllRenewalStoreMembership")
	ResponseEntity<RenewStoreMembershipResponse> getAllRenewalStoreMembership(
			@RequestParam(value = "status", required = false) Boolean status,
			@RequestParam(value = "planId", required = false) String planId,
			@RequestParam(value = "storeId", required = false) String storeId,
			@RequestParam(value = "orderId", required = false) String orderId,
			@RequestParam(value = "noOfDays", required = false) String noOfDays,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		RenewStoreMembershipResponse renewStoreMembershipResponse = new RenewStoreMembershipResponse();
		List<RenewalStoreMemberships> renewalStoreMembershipList = membershipHdrService
				.getAllRenewalStoreMembership(status, planId, storeId, orderId, noOfDays, pageNo, pageSize);
		renewStoreMembershipResponse.setRenewalStoreMembershipList(renewalStoreMembershipList);
		return ResponseEntity.status(HttpStatus.OK).body(renewStoreMembershipResponse);
	}
	
	@GetMapping("/getStoreLocation")
    public ResponseEntity<List<String>> getDistinctLocations(@RequestParam String storeCategory,@RequestParam(required = false) Map<String, String> requestParams) {
        List<String> distinctLocations = storeHandler.getDistinctLocationsByStoreCategory(storeCategory,requestParams);

        if (distinctLocations.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return 204 if no data found
        }

        return ResponseEntity.ok(distinctLocations);  // Return 200 OK with the list of locations
    }
	
	@GetMapping("/getStoreLocationEn")
    public ResponseEntity<List<String>> getDistinctLocationsEn(
            @RequestParam String storeCategory,
            @RequestParam(required = false) String storeBusinessType) {

        List<String> distinctLocations = storeHandler.getDistinctLocationsByStoreCategoryEn(storeCategory, storeBusinessType);

        if (distinctLocations.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(distinctLocations); // 200 OK
    }
	
	@GetMapping("/getStoreLocation-by-pharmacyAndDT")
	public ResponseEntity<List<String>> getDistinctLocationsEn() {
	    List<String> distinctLocations = storeHandler.getDistinctLocationsByStoreCategoryEn();

	    if (distinctLocations.isEmpty()) {
	        return ResponseEntity.noContent().build(); // 204 No Content
	    }

	    return ResponseEntity.ok(distinctLocations); // 200 OK
	}

	
	 @GetMapping("/getDistinctServiceCategory")
	    public ResponseEntity<List<String>> getServiceCategories(@RequestParam String storeCategory) {
	        List<String> serviceCategories = storeHandler.getServiceCategoriesByStoreCategory(storeCategory);
	        return ResponseEntity.ok(serviceCategories);
	    }
	
	 @GetMapping("/search-supplier-by-store-owner")
	    public ResponseEntity<List<StoreEntity>> getStoresByLocationAndBusinessType(
	            @RequestParam String location, @RequestParam String storeBusinessType) {
	        List<StoreEntity> stores = storeHandler.getStoresByLocationAndBusinessType(location, storeBusinessType);
	        if (stores.isEmpty()) {
	            return ResponseEntity.noContent().build();
	        }
	        return ResponseEntity.ok(stores);
	    }
	
	 @GetMapping("/get-storeinfo-by-locationAndStoreBusinessType")
	 public ResponseEntity<List<StoreResponseDTO>> getStoresByLocationAndBusinessTypeEn(
	         @RequestParam String location, @RequestParam String storeBusinessType) {

	     List<StoreResponseDTO> stores = storeHandler.getStoresByLocationAndBusinessTypeEn(location, storeBusinessType);

	     if (stores.isEmpty()) {
	         return ResponseEntity.noContent().build();
	     }
	     return ResponseEntity.ok(stores);
	 }
	 
	 @GetMapping("/distinct-store-business-types")
	    public ResponseEntity<List<String>> getDistinctStoreBusinessTypes() {
	        List<String> types = storeHandler.getDistinctStoreBusinessTypes();
	        return ResponseEntity.ok(types);
	    }
	 
	 @GetMapping("/store-business-types-by-location")
	    public ResponseEntity<List<String>> getStoreBusinessTypesByLocation(@RequestParam String location) {
	        List<String> types = storeHandler.getDistinctStoreBusinessTypesByLocation(location);
	        return ResponseEntity.ok(types);
	    }
	 
	 @GetMapping("/get-location-by-search")
		public ResponseEntity<Map<String, Object>> getlocationBySearch(@RequestParam(required = false) String location) {
			return ResponseEntity.ok(storeHandler.getlocationBySearch(location));
		}
	 
	 @PostMapping("/delete-storeId")
		public ResponseEntity<GenericResponse> servicePersonDeletAccoutn(@RequestBody DeleteStoreIdRequest deleteStoreId) {
			return new ResponseEntity<GenericResponse>(storeHandler.storeIdDelete(deleteStoreId),
					HttpStatus.CREATED);
		}
	 
	 @GetMapping("/pc/search-by-name")
	    public ResponseEntity<List<StoreResponseDto>> getStoresByName(@RequestParam(required = false) String storeName) {
	        List<StoreResponseDto> response = storeHandler.getPCStoresByName(storeName);
	        return ResponseEntity.ok(response);
	    }
	 
	 @GetMapping("/dc/search-by-name")
	    public ResponseEntity<List<StoreResponseDto>> getDCStoresByName(@RequestParam(required = false) String storeName) {
	        List<StoreResponseDto> response = storeHandler.getDCStoresByName(storeName);
	        return ResponseEntity.ok(response);
	    }
	 
	 @GetMapping("/ph/search-by-name")
	    public ResponseEntity<List<StoreResponseDto>> getPHStoresByName(@RequestParam(required = false) String storeName) {
	        List<StoreResponseDto> response = storeHandler.getPHStoresByName(storeName);
	        return ResponseEntity.ok(response);
	    }
}
