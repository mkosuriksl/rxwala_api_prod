package com.kosuri.stores.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.AdminDiagnosticServiceCategory;
import com.kosuri.stores.handler.AdminDiagnosticServiceCategoryService;
import com.kosuri.stores.model.dto.ResponseGetAdminDiagnosticServiceCategoryDto;

@RestController
public class AdminDiagnosticServiceCategoryController {
	
	@Autowired
	AdminDiagnosticServiceCategoryService adminDiagnosticServiceCategoryService;

	@PostMapping("/dc_service_category/add")
	public ResponseEntity<?> AddAdminDiagnosticServiceCategory(@RequestBody List<AdminDiagnosticServiceCategory> addAdminDiagnosticServiceCategoryList) {
		return new ResponseEntity<>(adminDiagnosticServiceCategoryService.addAdminDiagnosticServiceCategory(addAdminDiagnosticServiceCategoryList), HttpStatus.OK);
	}
	
	@GetMapping("/dc_service_category/get")
//	public ResponseEntity<List<AdminDiagnosticServiceCategory>> getAdminDiagnosticServiceCategory(
//            @RequestParam(required = false) String dcServiceCategoryId,
//            @RequestParam(required = false) String dcServiceCategoryName,
//            @RequestParam(required = false) String updatedBy,
//			@RequestParam(required = false) Map<String, String> requestParams) {
//		List<AdminDiagnosticServiceCategory> updatedRecords = adminDiagnosticServiceCategoryService.getAdminDiagnosticServiceCategory(dcServiceCategoryId, dcServiceCategoryName, updatedBy, requestParams);
//		return ResponseEntity.ok(updatedRecords);
//	}
	public ResponseEntity<ResponseGetAdminDiagnosticServiceCategoryDto> getTask(
			@RequestParam(required = false) String dcServiceCategoryId, 
			@RequestParam(required = false) String dcServiceCategoryName,
			@RequestParam(required = false) String updatedBy, 
			@RequestParam(required = false) Map<String, String> requestParams,
		     @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<AdminDiagnosticServiceCategory> srpqPage = 
				adminDiagnosticServiceCategoryService.getAdminDiagnosticServiceCategory
				(dcServiceCategoryId, dcServiceCategoryName,updatedBy,requestParams,
				 pageable);
		ResponseGetAdminDiagnosticServiceCategoryDto response = new ResponseGetAdminDiagnosticServiceCategoryDto();

		response.setMessage("Admin Diagnostic Service Category details retrieved successfully.");
		response.setStatus(true);
		response.setAdminDignosticServiceCategory(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/get_dc_service_category_home")
	public List<String> getDcServiceCategoryHomeDistinct() {
		return adminDiagnosticServiceCategoryService.getDcServiceCategoryHomeDistinct();
	}
	
	@GetMapping("/get_dc_service_category_home-by-search")
	public ResponseEntity<Map<String, Object>> getServiceCategoryBySearch(@RequestParam(required = false) String serviceCategory) {
		return ResponseEntity.ok(adminDiagnosticServiceCategoryService.getServiceCategoryBySearch(serviceCategory));
	}
	
	@PutMapping("/dc_service_category/update")
	public ResponseEntity<List<AdminDiagnosticServiceCategory>> updateAdminDiagnosticServiceCategory(@RequestBody List<AdminDiagnosticServiceCategory> updateAdminDiagnosticServiceCategory) {
	    List<AdminDiagnosticServiceCategory> updatedRecords = adminDiagnosticServiceCategoryService.updateAdminDiagnosticServiceCategory(updateAdminDiagnosticServiceCategory);
	    return ResponseEntity.ok(updatedRecords);
	}

}
