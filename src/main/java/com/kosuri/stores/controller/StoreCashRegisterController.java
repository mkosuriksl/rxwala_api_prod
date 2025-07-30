package com.kosuri.stores.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.StoreCashRegisterEntity;
import com.kosuri.stores.handler.StoreCashRegisterHandler;
import com.kosuri.stores.model.request.StoreCashRequest;
import com.kosuri.stores.model.response.GenericResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/store-cash")
public class StoreCashRegisterController {

	@Autowired
	private StoreCashRegisterHandler cashRegisterHandler;

	@GetMapping("/getAll")
	public ResponseEntity<List<StoreCashRegisterEntity>> getAllStoreCashByStoreId(@RequestParam String storeId) {
		log.info(">>Controller Logger getAllStoreCashByStoreId({})");
		return ResponseEntity.status(HttpStatus.OK).body(cashRegisterHandler.getAllStoreCashByStoreId(storeId));
	}

	@GetMapping("/{storeCashId}")
	public ResponseEntity<StoreCashRegisterEntity> getStoreCashByStoreIdAndByStoreCashId(@RequestParam String storeId,
			@PathVariable Long storeCashId) {
		log.info(">>Controller Logger getStoreCashByStoreIdAndByStoreCashId({})", storeId, storeCashId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(cashRegisterHandler.getStoreCashByStoreIdAndByStoreCashId(storeId, storeCashId));
	}

	@PostMapping("/add")
	public ResponseEntity<GenericResponse> saveStoreCash(@RequestBody StoreCashRequest cashRequest) {
		log.info(">>Controller Logger save saveStoreCash({})", cashRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(cashRegisterHandler.saveStoreCash(cashRequest));

	}

}
