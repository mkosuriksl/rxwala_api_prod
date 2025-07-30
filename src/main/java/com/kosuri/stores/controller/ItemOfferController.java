package com.kosuri.stores.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.ItemOfferEntity;
import com.kosuri.stores.handler.ItemOfferService;
import com.kosuri.stores.model.dto.GenericResponse;
import com.kosuri.stores.model.dto.ItemOfferRequestDTO;

@RestController
@RequestMapping("/api/item-offer")
public class ItemOfferController {

	@Autowired
	private ItemOfferService service;

	
	@PostMapping("/add")
    public ResponseEntity<?> saveItemOffers(@RequestBody ItemOfferRequestDTO requestDto) {
        return service.saveItemOffers(requestDto);
    }
	
//	@PostMapping
//	public ResponseEntity<GenericResponse<ItemOfferEntity>> addOffer(@RequestBody ItemOfferEntity entity) {
//		ItemOfferEntity savedOffer = service.addOffer(entity);
//		GenericResponse<ItemOfferEntity> response = new GenericResponse<>("success", "Item offer added successfully",
//				savedOffer);
//		return ResponseEntity.ok(response);
//	}
	
	

	@PutMapping
	public ResponseEntity<GenericResponse<List<ItemOfferEntity>>> updateOffer(@RequestBody List<ItemOfferEntity> updatedEntity) {
		List<ItemOfferEntity> updatedOffer = service.updateOffer(updatedEntity);
		GenericResponse<List<ItemOfferEntity>> response = new GenericResponse<>("success", "Item offer updated successfully",
				updatedOffer);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<GenericResponse<List<ItemOfferEntity>>> getOffer(
			@RequestParam(required = false) String userIdStoreIdItemCode,
			@RequestParam(required = false) String batchNumber, @RequestParam(required = false) String discount,
			@RequestParam(required = false) String userId,@RequestParam(required = false) String storeId,@RequestParam(required = false)String userIdStoreId,
			@RequestParam(required = false) Map<String, String> requestParams) {

		List<ItemOfferEntity> offers = service.getOffer(userIdStoreIdItemCode, batchNumber, discount, userId,storeId,userIdStoreId,
				requestParams);

		GenericResponse<List<ItemOfferEntity>> response = new GenericResponse<>("success",
				"Item offer retrieved successfully", offers);
		return ResponseEntity.ok(response);
	}

}
