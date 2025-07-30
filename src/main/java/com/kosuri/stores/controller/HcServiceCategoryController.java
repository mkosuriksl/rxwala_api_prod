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

import com.kosuri.stores.dao.HcServiceCategory;
import com.kosuri.stores.handler.HcServiceCategoryService;
import com.kosuri.stores.model.dto.ResponseHcServiceCategoryDto;
import com.kosuri.stores.model.dto.ResponseHcServiceCategoryGetDto;

@RestController
@RequestMapping("/rxwala")
public class HcServiceCategoryController {

	@Autowired
	private HcServiceCategoryService hcServiceCategoryService;

	@PostMapping("/add-hcServiceCategory")
	public ResponseEntity<ResponseHcServiceCategoryDto> addDlerUrl(@RequestBody HcServiceCategory hcServiceCategor) {
	    HcServiceCategory hcServiceCategory = hcServiceCategoryService.addHcServiceCategory(hcServiceCategor);
	    ResponseHcServiceCategoryDto response = new ResponseHcServiceCategoryDto();
	    response.setMessage("Hc Service Category details added successfully");
	    response.setStatus(true);
	    response.setData(hcServiceCategory);    
	    return ResponseEntity.ok(response);
	}

	@PutMapping("/update-hcServiceCategory")
	public ResponseEntity<?> updateHcServiceCategory(@RequestBody HcServiceCategory hcServiceCategor) {
		ResponseHcServiceCategoryDto response = new ResponseHcServiceCategoryDto();
		try {
			HcServiceCategory hcServiceCategory = hcServiceCategoryService.updateHcServiceCategory(hcServiceCategor);
			if (hcServiceCategory!=null) {
				response.setMessage("Updated successfully");
				response.setStatus(true);
				response.setData(hcServiceCategory);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("Failed to update/Please check your  dleruiUrl");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);

		}
	}
	
	@GetMapping("/get-hcServiceCategory")
	public ResponseEntity<?> getHcServiceCategory(@RequestParam(required = false) String storeId,
			@RequestParam(required = false) String serviceCategoryId,
			@RequestParam(required = false) String serviceCategoryName,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) Map<String, String> requestParams) {

		ResponseHcServiceCategoryGetDto response = new ResponseHcServiceCategoryGetDto();

		try {
			List<HcServiceCategory> hcServiceCategories = hcServiceCategoryService.getHcServiceCategory(storeId,serviceCategoryId, serviceCategoryName,userId,requestParams);

			if (hcServiceCategories != null || hcServiceCategories.isEmpty()) {
				response.setMessage("Hc Service Categories details");
				response.setStatus(true);
				response.setData(hcServiceCategories);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("No details found for given parameters/check your parameters");
				response.setStatus(false);
				response.setData(hcServiceCategories);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());

		}
	}
	
	@GetMapping("/get-hcdistinctServiceCategory")
	List<String>getHcDistinctServiceCategory(){
		return hcServiceCategoryService.getHcDistinctServiceCategory();
	}
}
