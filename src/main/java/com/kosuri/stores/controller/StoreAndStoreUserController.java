package com.kosuri.stores.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.StoreAndStoreUserEntity;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreUserUpdateResponseDto;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.StoreAndStoreUserService;
import com.kosuri.stores.handler.StoreHandler;
import com.kosuri.stores.handler.UserHandler;
import com.kosuri.stores.model.dto.GenericResponse;
import com.kosuri.stores.model.dto.ResponseStoreAndStoreUserDto;
import com.kosuri.stores.model.request.AddTabStoreAndStoreUserRequest;
import com.kosuri.stores.model.request.LoginUserRequest;
import com.kosuri.stores.model.response.CreateStoreResponse2;
import com.kosuri.stores.model.response.LoginStoreAndStoreUserResponse;

@RestController
@RequestMapping("/api/")
public class StoreAndStoreUserController {
	@Autowired
	UserHandler userHandler;

	@Autowired
    private StoreAndStoreUserService storeAndStoreUserService;

	@Autowired
	private StoreHandler storeHandler;
    // User Registration
    @PostMapping("storeAndStoreUserRegister")
    public ResponseEntity<CreateStoreResponse2> registerUser(@RequestBody AddTabStoreAndStoreUserRequest request) {
        return storeAndStoreUserService.registerUser(request);
    }
    
    @PostMapping("storeAndStoreUserLogin")
	public ResponseEntity<LoginStoreAndStoreUserResponse> login(@RequestBody LoginUserRequest request) {
		HttpStatus httpStatus;
		LoginStoreAndStoreUserResponse response = new LoginStoreAndStoreUserResponse();
		try {
			response = userHandler.loginStoreAndStoreUser(request);
			httpStatus = HttpStatus.OK;
			response.setResponseMessage("User logged in successfully!");
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}
		return ResponseEntity.status(httpStatus).body(response);
	}
    
    @GetMapping("getSUUsers")
    public ResponseEntity<List<Map<String, Object>>> getStoreDetails(@RequestParam(required = false) String userId) {
        List<Map<String, Object>> responseList = storeAndStoreUserService.getStoreDetailsByUserId(userId);
        return ResponseEntity.ok(responseList);
    }
    
    @GetMapping("get-SUUsers")
	public ResponseEntity<ResponseStoreAndStoreUserDto> getServicePerson(
			@RequestParam(required = false) String suUserId, @RequestParam(required = false) String storeId,@RequestParam(required = false) String userIdstoreId,
			@RequestParam(required = false) String storeUserContact, @RequestParam(required = false) String storeUserEmail) {

		List<StoreAndStoreUserEntity> storeAndStoreUser = storeAndStoreUserService.getStoreAndStorUser(suUserId, storeId);
		List<TabStoreUserEntity> storeTabUser = storeHandler.getStoreTabUser(suUserId, userIdstoreId,storeUserContact,storeUserEmail);
		List<StoreEntity>storeInfo=storeHandler.getStoreInfoByStoreId(storeId);
		ResponseStoreAndStoreUserDto response = new ResponseStoreAndStoreUserDto();
		try {
			if (!storeAndStoreUser.isEmpty()) {
				response.setMessage("Received store and store user details");
				response.setStatus(true);
				response.setStoreAndStoreUser(storeAndStoreUser);
				response.setStoreTabUser(storeTabUser);
				response.setStoreInfo(storeInfo);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("No details found. Check your suUserId/storeId");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			response.setStoreAndStoreUser(storeAndStoreUser);
			response.setStoreTabUser(storeTabUser);
			response.setStoreInfo(storeInfo);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
    }
    
    @PutMapping("update-storeAndStoreUser")
    public ResponseEntity<GenericResponse<List<StoreUserUpdateResponseDto>>> updateStoreUser(
            @RequestBody List<TabStoreUserEntity> storeAndStoreUsers) {

        List<StoreUserUpdateResponseDto> responseList = storeAndStoreUserService.updateStoreUser(storeAndStoreUsers);

        GenericResponse<List<StoreUserUpdateResponseDto>> response = new GenericResponse<>(
                "Successfully",
                "StoreAndStoreUser Updated successfully",
                responseList
        );

        return ResponseEntity.ok(response);
    }


}
