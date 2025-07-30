package com.kosuri.stores.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.StoreAdminBrandEntity;
import com.kosuri.stores.handler.StoreAdminBrandHandler;
import com.kosuri.stores.model.dto.StoreAdminBrandRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/store-admin-brand")
public class StoreAdminBrandController {

	@Autowired
	private StoreAdminBrandHandler adminBrandHandler;

	@GetMapping("/getAll")
	public ResponseEntity<List<StoreAdminBrandEntity>> getStoreAdminBrands() {
		log.info(">> getStoreAdminBrands({})");
		return ResponseEntity.ok(adminBrandHandler.getStoreAdminBrands());
	}

	@GetMapping("/{id}")
	public ResponseEntity<StoreAdminBrandEntity> getStoreAdminBrandById(@PathVariable String id) {
		log.info(">> getContactById({})", id);
		return ResponseEntity.ok(adminBrandHandler.getStoreAdminBrandById(id));
	}

	@PostMapping("/save")
	public ResponseEntity<Map<String, Object>> saveStoreAdminBrand(@RequestParam String adminEmail,
			@RequestBody StoreAdminBrandRequest storeAdminBrand) {
		log.info(">> saveScheme({})", storeAdminBrand);
		return ResponseEntity.ok(adminBrandHandler.saveStoreAdminBrand(adminEmail,storeAdminBrand));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateStoreAdminBrand(@PathVariable String id,
			@RequestBody StoreAdminBrandRequest req) {
		log.info(">> updateScheme({}, {})", id, req);
		return ResponseEntity.ok(adminBrandHandler.updateStoreAdminBrand(id, req));
	}

	@DeleteMapping("delete/{id}")
	public ResponseEntity<Map<String, Object>> deleteStoreAdminBrand(@PathVariable String id) {
		log.info(">> updateScheme({}, {})", id);
		return ResponseEntity.ok(adminBrandHandler.deleteStoreAdminBrand(id));
	}

}
