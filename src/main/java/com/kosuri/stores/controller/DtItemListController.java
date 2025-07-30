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

import com.kosuri.stores.dao.DtItemList;
import com.kosuri.stores.handler.DtItemListService;
import com.kosuri.stores.model.dto.DtItemListDto;

@RestController
public class DtItemListController {

	@Autowired
	private DtItemListService dtItemListService;

	@PostMapping("/add-dtItemList")
	public ResponseEntity<DtItemListDto> createDtItemList(@RequestBody DtItemListDto dtItemListDto) {
		DtItemListDto createduerServiceCatgoryTableDto = dtItemListService.createDtItemList(dtItemListDto);
		return new ResponseEntity<>(createduerServiceCatgoryTableDto, HttpStatus.CREATED);
	}

	@PutMapping("/update-dtItemList")
	public ResponseEntity<DtItemListDto> updateDtItemList(
			@RequestBody DtItemListDto dtItemListDto) {
		DtItemListDto updatedDtItemListDto = dtItemListService
				.updateDtItemList(dtItemListDto);
		return new ResponseEntity<>(updatedDtItemListDto, HttpStatus.OK);
	}


	@GetMapping("/get-dtItemList")
	public ResponseEntity<List<DtItemList>> findAll(@RequestParam(required = false) String itemCategory,
			@RequestParam(required = false) String itemSubcategory, @RequestParam(required = false) String itemCode,
			@RequestParam(required = false) String brand, @RequestParam(required = false) String manufacturer,
			@RequestParam(required = false) Map<String, String> requestParams) {

		List<DtItemList> result = dtItemListService.findAll(itemCategory, itemSubcategory, itemCode, brand,
				manufacturer, requestParams);
		return ResponseEntity.ok(result);
	}
}
