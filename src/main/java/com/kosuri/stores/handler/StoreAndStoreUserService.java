package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kosuri.stores.constant.StoreConstants;
import com.kosuri.stores.dao.StoreAndStoreUserEntity;
import com.kosuri.stores.dao.StoreAndStoreUserRepo;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.StoreUserUpdateResponseDto;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.StoreDetailsDto;
import com.kosuri.stores.model.dto.TabStoreUserDTO;
import com.kosuri.stores.model.enums.UserType;
import com.kosuri.stores.model.request.AddTabStoreAndStoreUserRequest;
import com.kosuri.stores.model.response.CreateStoreResponse2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class StoreAndStoreUserService {

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@Autowired
	private StoreAndStoreUserRepo storeAndUserRepository;

	BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private StoreRepository storeRepository;

	// **User Registration**
	public ResponseEntity<CreateStoreResponse2> registerUser(AddTabStoreAndStoreUserRequest request) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
		}
		HttpStatus httpStatus;
		CreateStoreResponse2 response = new CreateStoreResponse2();
		
		try {
			// Generate userId
			String userId = generateUserId();

			// Check for existing user by email or phone
			Optional<TabStoreUserEntity> existingUser = tabStoreRepository
			        .findByStoreUserEmailOrStoreUserContact(request.getUserEmail(), request.getUserPhoneNumber());

			if (existingUser.isPresent()) {
			    String existingField = "";

			    if (existingUser.get().getStoreUserEmail().equals(request.getUserEmail())) {
			        existingField = "Email already exists.";
			    } else if (existingUser.get().getStoreUserContact().equals(request.getUserPhoneNumber())) {
			        existingField = "Phone number already exists.";
			    } else {
			        existingField = "Email or phone already exists.";
			    }

			    response.setResponseMessage("Error: " + existingField);
			    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			}

			// Create store user entity
			TabStoreUserEntity storeUser = new TabStoreUserEntity();
			storeUser.setUserId(userId);
			storeUser.setUsername(request.getUserFullName());
			storeUser.setStoreUserEmail(request.getUserEmail());
			storeUser.setStoreUserContact(request.getUserPhoneNumber());
			storeUser.setPassword(byCrypt.encode(request.getPassword())); // Encrypt password
			storeUser.setStatus("ACTIVE");
			storeUser.setUserType(UserType.SU.toString()); // Setting user type as SU
			storeUser.setRegistrationDate(LocalDateTime.now());
			storeUser.setAddedBy(loginStore.get().getUserId());
			storeUser.setStoreAdminEmail(loginStore.get().getStoreAdminEmail());
			storeUser.setStoreAdminContact(loginStore.get().getStoreAdminContact());

			// Save user in tab_store_user_login table
			tabStoreRepository.save(storeUser);

			// Create store_and_store_user entity
			StoreAndStoreUserEntity storeAndUser = new StoreAndStoreUserEntity();
			storeAndUser.setSuUserId(userId);
			storeAndUser.setStoreId(request.getStoreId()); // Assuming storeId is provided in request
			storeAndUser.setUserIdstoreId(userId + "_" + request.getStoreId());
			storeAndUser.setUpdatedBy(loginStore.get().getUserId());
			storeAndUser.setUpdatedDate(new Date());

			// Save in store_and_store_user table
			StoreAndStoreUserEntity save=storeAndUserRepository.save(storeAndUser);

			response.setResponseMessage("User registered successfully");
//			response.setUserIdStoreId(save.getUserIdstoreId());
			httpStatus = HttpStatus.OK;

		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage("Error during registration: " + e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

	private String generateUserId() {
		LocalDateTime timestamp = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timestampStr = timestamp.format(formatter);
		return StoreConstants.RX_SUCONSTANT + "_" + timestampStr + "_" + OtpHandler.generateOTP(false);
	}

	public List<Map<String, Object>> getStoreDetailsByUserId(String userId) {
	    List<Map<String, Object>> responseList = new ArrayList<>();

	    // Step 1: Get storeAndStoreUser records using userId (updatedBy field)
	    List<StoreAndStoreUserEntity> storeUserList = storeAndUserRepository.findByUpdatedBy(userId);
	    if (storeUserList.isEmpty()) {
	        return responseList;  // Return empty list if no store found
	    }

	    // Step 2: Iterate through all storeAndStoreUser records
	    for (StoreAndStoreUserEntity storeUser : storeUserList) {
	        String storeId = storeUser.getStoreId();
	        String userIdStoreId = userId + "_" + storeId;

	        // Step 3: Fetch all Store Info using userId_storeId
	        List<StoreEntity> storeInfoList = storeRepository.findByUserIdStoreIds(userIdStoreId);
	        
	        // Step 4: Get su_userId_storeId from store_and_store_user
	        String suUserIdStoreId = storeUser.getUserIdstoreId();
	        String suUserId = suUserIdStoreId.substring(0, suUserIdStoreId.lastIndexOf("_"));

	        // Step 5: Find store user details using suUserId (include all matches)
	        List<TabStoreUserEntity> storeUserLoginList = tabStoreRepository.findByUserIdOne(suUserId);
	        if (storeUserLoginList.isEmpty()) {
	            continue; // Skip if no user login details found
	        }

	        // Step 6: Iterate through all store user login records and map the response
	        for (TabStoreUserEntity storeUserLogin : storeUserLoginList) {
	            Map<String, Object> storeUserMap = new HashMap<>();
	            
	            // Convert TabStoreUserEntity to TabStoreUserDTO
	            TabStoreUserDTO storeUserDto = new TabStoreUserDTO(
	                storeUserLogin.getUserId(),
	                storeUserLogin.getUsername(),
	                storeUserLogin.getStoreUserContact(),
	                storeUserLogin.getStoreUserEmail(),
	                storeUserLogin.getRegistrationDate(),
	                storeUserLogin.getAddedBy(),
	                storeUserLogin.getStoreAdminEmail(),
	                storeUserLogin.getStoreAdminContact(),
	                storeUserLogin.getStatus(),
	                storeUserLogin.getUserType()
	            );
	            storeUserMap.put("storeUserLogin", storeUserDto);
	            
	            List<StoreDetailsDto> storeDetailsList = new ArrayList<>();

	            // If no store info found, return empty list but include storeUserLogin
	            if (!storeInfoList.isEmpty()) {
	                for (StoreEntity storeInfo : storeInfoList) {
	                    StoreDetailsDto storeDto = new StoreDetailsDto(
	                            storeInfo.getType(),
	                            storeInfo.getUserIdStoreId(),
	                            storeInfo.getId(),
	                            storeInfo.getName(),
	                            storeInfo.getPincode(),
	                            storeInfo.getDistrict(),
	                            storeInfo.getState(),
	                            storeInfo.getLocation(),
	                            storeInfo.getOwner(),
	                            storeInfo.getOwnerContact(),
	                            storeInfo.getOwnerEmail()
	                    );
	                    storeDetailsList.add(storeDto);
	                }
	            }
	            storeUserMap.put("storeInfo", storeDetailsList); // Empty list if no match
	            responseList.add(storeUserMap);
	        }
	    }
	    return responseList;
	}

	public List<StoreAndStoreUserEntity> getStoreAndStorUser(String suUserId, String storeId) {
	    if (suUserId != null && storeId != null) {
	        return storeAndUserRepository.findBySuUserIdAndStoreId(suUserId, storeId); // <-- FIXED
	    } else if (suUserId != null) {
	        return storeAndUserRepository.findBySuUserId(suUserId);
	    } else if (storeId != null) {
	        return storeAndUserRepository.findByStoreId(storeId);
	    } else {
	        return storeAndUserRepository.findAll(); // support no param
	    }
	}

	public List<StoreUserUpdateResponseDto> updateStoreUser(List<TabStoreUserEntity> storeAndStoreUsers) {
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);

	    if (loginStore.isEmpty()) {
	        throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
	    }

	    List<StoreUserUpdateResponseDto> responseList = new ArrayList<>();

	    for (TabStoreUserEntity inputItem : storeAndStoreUsers) {
	        String suUserId = inputItem.getUserId();

	        if (suUserId == null || suUserId.isEmpty()) {
	            throw new ResourceNotFoundException("UserId is required for update.");
	        }

	        List<TabStoreUserEntity> existingRecords = tabStoreRepository.findByUserIdOne(suUserId);
	        if (existingRecords.isEmpty()) {
	            throw new ResourceNotFoundException("No record found for userId: " + suUserId);
	        }

	        for (TabStoreUserEntity record : existingRecords) {
	            record.setUsername(inputItem.getUsername());
	            tabStoreRepository.save(record); // save update

	            Optional<StoreAndStoreUserEntity> storeUserOpt = storeAndUserRepository.findById(suUserId);
	            if (storeUserOpt.isPresent()) {
	                StoreAndStoreUserEntity storeUser = storeUserOpt.get();
	                storeUser.setUpdatedBy(loggedInUserEmail);
	                storeUser.setUpdatedDate(new Date());
	                storeAndUserRepository.save(storeUser);

	                StoreUserUpdateResponseDto dto = new StoreUserUpdateResponseDto();
	                dto.setSuUserId(storeUser.getSuUserId());
	                dto.setUserIdstoreId(storeUser.getUserIdstoreId());
	                dto.setStoreId(storeUser.getStoreId());
	                dto.setUpdatedBy(storeUser.getUpdatedBy());
	                dto.setUpdatedDate(storeUser.getUpdatedDate());
	                dto.setUsername(record.getUsername());
	                dto.setAddedBy(existingRecords.get(0).getAddedBy());
	                dto.setStoreUserContact(existingRecords.get(0).getStoreUserContact());
	                dto.setStoreUserEmail(existingRecords.get(0).getStoreUserEmail());
	                dto.setUserType(existingRecords.get(0).getUserType());
	                dto.setStatus(existingRecords.get(0).getStatus());
	                responseList.add(dto);
	            }
	        }
	    }

	    return responseList;
	}


}
