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

import com.kosuri.stores.dao.AdminStoreBusinessTypeEntity;
import com.kosuri.stores.handler.AdminStoreBusinessTypeService;
import com.kosuri.stores.model.dto.ResponseAdminStoreBusinessTypeDto;

@RestController
@RequestMapping("/api/adminStoreBusinessType")
public class AdminStoreBusinessTypeController {

	@Autowired
	private AdminStoreBusinessTypeService adminStoreBusinessTypeService;

	@PostMapping("/add")
	public ResponseEntity<ResponseAdminStoreBusinessTypeDto> create(@RequestBody AdminStoreBusinessTypeEntity entity) {
		return new ResponseEntity<>(adminStoreBusinessTypeService.createBusinessType(entity), HttpStatus.CREATED);
	}

	@GetMapping("/get")
	public ResponseEntity<List<AdminStoreBusinessTypeEntity>> getAdminStoreBusinessTypes(
			@RequestParam(required = false) String businessTypeId, @RequestParam(required = false) String businessName,
			@RequestParam Map<String, String> requestParams) {

		List<AdminStoreBusinessTypeEntity> businessTypes = adminStoreBusinessTypeService
				.getAdminStoreBusinessType(businessTypeId, businessName, requestParams);

		return ResponseEntity.ok(businessTypes);
	}
	
	@PutMapping("/update")
	public ResponseEntity<AdminStoreBusinessTypeEntity> updateAdminStoreBusinessType(@RequestBody AdminStoreBusinessTypeEntity entity) {
	    AdminStoreBusinessTypeEntity updatedEntity = adminStoreBusinessTypeService.updateAdminStoreBusinessType(entity);
	    
	    if (updatedEntity == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	    
	    return ResponseEntity.ok(updatedEntity);
	}

}
