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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.UserServiceCatgoryTable;
import com.kosuri.stores.handler.UserServiceCategoryTableService;
import com.kosuri.stores.model.dto.UserServiceCatgoryTableDto;

@RestController
public class UserServiceCategoryTableController {

	@Autowired
	private UserServiceCategoryTableService userServiceCategoryTableService;

	@PostMapping("/add-userServiceCategory")
	public ResponseEntity<UserServiceCatgoryTableDto> createCarSaleMaster(
			@RequestBody UserServiceCatgoryTableDto uerServiceCatgoryTableDto) {
		UserServiceCatgoryTableDto createduerServiceCatgoryTableDto = userServiceCategoryTableService
				.createUserServiceCatgoryTableDto(uerServiceCatgoryTableDto);
		return new ResponseEntity<>(createduerServiceCatgoryTableDto, HttpStatus.CREATED);
	}

	@PutMapping("/update-userServiceCategory")
	public ResponseEntity<UserServiceCatgoryTableDto> updateUserServiceCatgoryTable(
			@RequestBody UserServiceCatgoryTableDto uerServiceCatgoryTableDto) {
		UserServiceCatgoryTableDto updatedUserServiceCatgoryTableDto = userServiceCategoryTableService
				.updateUserServiceCatgoryTable(uerServiceCatgoryTableDto);
		return new ResponseEntity<>(updatedUserServiceCatgoryTableDto, HttpStatus.OK);
	}

	@GetMapping("/get-userServiceCategory")
	  public ResponseEntity<List<UserServiceCatgoryTable>> findAll(
	            @RequestParam(required=false) String userId,
	            @RequestParam(required = false) Map<String, String> requestParams) {
	        
	        List<UserServiceCatgoryTable> result = userServiceCategoryTableService.findAll(userId, requestParams);
	        return ResponseEntity.ok(result);
	    }
}
