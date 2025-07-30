package com.kosuri.stores.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.handler.StoreUserHandler;
import com.kosuri.stores.model.request.LoginUserRequest;
import com.kosuri.stores.model.response.LoginUserResponse;
import com.kosuri.stores.model.response.MaterialDetailResponse;
import com.kosuri.stores.model.response.StoreInfoResponse;
import com.kosuri.stores.utils.CurrentUser;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/store-user")
@Slf4j
public class StoreUserController {

	@Autowired
	private StoreUserHandler storeUserHandler;

	@PostMapping("/login")
	public ResponseEntity<LoginUserResponse> loginStoreUser(@Valid @RequestBody LoginUserRequest request)
			throws Exception {
		log.info(">> authenticate OR loginStoreUser({})", request);
		return ResponseEntity.ok(storeUserHandler.loginStoreUser(request));
	}

	@GetMapping("/get-store")
	public ResponseEntity<StoreEntity> getLoggedInUserStoreInfo() {
		log.info(">> getLoggedInUserStoreInfo({})");
		String email = CurrentUser.getEmail();
		return ResponseEntity.ok(storeUserHandler.getLoggedInUserStoreInfo(email));
	}

	@GetMapping("/search-store")
	public ResponseEntity<List<StoreInfoResponse>> getStoreInfoDetails(@RequestParam(required = false) String storeName,
			@RequestParam(required = false) String businessType, @RequestParam(required = false) String location) {
		log.info(">> getStoreInfoDetails({}, {}, {})", storeName, businessType, location);
		return ResponseEntity.ok(storeUserHandler.getStoreInfoDetails(storeName, businessType, location));
	}

	@GetMapping("/material-details/{storeId}")
	public ResponseEntity<List<MaterialDetailResponse>> getMaterialDetail(@PathVariable String storeId,
			@RequestParam(required = false) String itemCategory, @RequestParam(required = false) String itemName) {
		log.info(">> getMaterialDetail({})", storeId, itemCategory, itemName);
		return ResponseEntity.ok(storeUserHandler.getMaterialDetail(storeId, itemCategory, itemName));
	}

}
