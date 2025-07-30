package com.kosuri.stores.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosuri.stores.dao.ItemCodeMaster;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.ItemCodeMasterService;
import com.kosuri.stores.model.dto.GenericResponse;
import com.kosuri.stores.model.dto.ItemCodeSearchDTO;
import com.kosuri.stores.model.dto.ResponseGetItemCodeMasterDto;
import com.kosuri.stores.model.response.CreateStoreResponse;
import com.kosuri.stores.model.response.GenericResponse2;

@RestController
@RequestMapping("/api/item")
public class ItemCodeMasterController {

    @Autowired
    private ItemCodeMasterService itemService;

    @PostMapping("/add")
    public ResponseEntity<GenericResponse<List<ItemCodeMaster>>> addItems(@RequestBody List<ItemCodeMaster> items) {
        GenericResponse<List<ItemCodeMaster>> response = itemService.saveItems(items);
        return ResponseEntity.ok(response); // Always returns HTTP 200
    }


    @PutMapping("/update")
    public ResponseEntity<List<ItemCodeMaster>> updateItemNames(@RequestBody List<ItemCodeMaster> itemList) {
        List<ItemCodeMaster> updatedItems = itemService.updateItemNames(itemList);
        return ResponseEntity.ok(updatedItems);
    }

    @GetMapping("/get")
	public ResponseEntity<ResponseGetItemCodeMasterDto> get(
			@RequestParam(required = false) String userIdStoreIdItemCode, @RequestParam(required = false) String storeId,
			@RequestParam(required = false) String itemCode, @RequestParam(required = false) String itemName,
			@RequestParam(required = false) String itemCategory, @RequestParam(required = false) String itemSubCategory,
			@RequestParam(required = false) String manufacturer, @RequestParam(required = false) String brand,
			@RequestParam(required = false) String hsnGroup,@RequestParam(required = false) String userId, @RequestParam(required = false) String userIdStoreId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<ItemCodeMaster> srpqPage = itemService.get(userIdStoreIdItemCode, storeId, itemCode, itemName,
				itemCategory,itemSubCategory,manufacturer,brand,hsnGroup,userId,userIdStoreId, pageable);
		ResponseGetItemCodeMasterDto response = new ResponseGetItemCodeMasterDto();

		response.setMessage("item code master details retrieved successfully.");
		response.setStatus(true);
		response.setItemCodeMasters(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
    
    @GetMapping("/search-by-itemcode")
    public List<ItemCodeSearchDTO> searchByItemCode(@RequestParam String itemCode) {
        return itemService.searchByItemCode(itemCode);
    }
    
    @GetMapping("/search-by-itemname")
    public List<ItemCodeSearchDTO> searchByItemName(@RequestParam String itemName) {
        return itemService.searchByItemName(itemName);
    }
    
    @PostMapping("/item-code-master-image")
    public ResponseEntity<GenericResponse2> uploadMedicinePhoto(
            @RequestParam("image1") MultipartFile image1, @RequestParam("image2") MultipartFile image2, @RequestParam("image2") MultipartFile image3,
            @RequestParam("userIdStoreIdItemCode") String userIdStoreIdItemCode) {

    	GenericResponse2 response = new GenericResponse2();

        try {
            // Call service method to upload and save photo
        	itemService.uploadFilesAndSaveFileLink(image1,image2,image3, userIdStoreIdItemCode);

            response.setResponseMessage("Medicine photo uploaded successfully for: " + userIdStoreIdItemCode);
            return ResponseEntity.ok(response);

        } catch (APIException e) {
            // Custom business-level exception
            response.setResponseMessage("Upload failed for " + userIdStoreIdItemCode + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            // Generic failure
            response.setResponseMessage("Internal error for " + userIdStoreIdItemCode + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    
}
