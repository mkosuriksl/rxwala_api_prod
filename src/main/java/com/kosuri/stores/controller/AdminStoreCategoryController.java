package com.kosuri.stores.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.AdminStoreCategory;
import com.kosuri.stores.handler.AdminStoreCategoryService;
import com.kosuri.stores.model.dto.AdminStoreCategoryDto;

@RestController
public class AdminStoreCategoryController {

	@Autowired
	private AdminStoreCategoryService adminStoreCategoryService;

	@PostMapping("/add-adminStoreCategory")
	public ResponseEntity<AdminStoreCategoryDto> createAdminStoreCategor(@RequestBody AdminStoreCategoryDto adminStoreCategoryDto) {
		AdminStoreCategoryDto createAdminStoreCategoryDto = adminStoreCategoryService.createAdminStoreCategory(adminStoreCategoryDto);
		return new ResponseEntity<>(createAdminStoreCategoryDto, HttpStatus.CREATED);
	}

	@PutMapping("/update-adminStoreCategory")
	public ResponseEntity<AdminStoreCategoryDto> updateAdminStoreCategory(
			@RequestBody AdminStoreCategoryDto adminStoreCategoryDto) {
				AdminStoreCategoryDto updatedAdminStoreCategory = adminStoreCategoryService
				.updateAdminStoreCategory(adminStoreCategoryDto);
		return new ResponseEntity<>(updatedAdminStoreCategory, HttpStatus.OK);
	}
	@GetMapping("/all-adminStoreCategory")
    public ResponseEntity<List<AdminStoreCategory>> getAllAdminStoreCategories() {
        List<AdminStoreCategory> categories = adminStoreCategoryService.getAllAdminStoreCategories();
        return ResponseEntity.ok(categories);
    }
}
