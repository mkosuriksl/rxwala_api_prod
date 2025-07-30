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

import com.kosuri.stores.dao.ItemDiscountCurrent;
import com.kosuri.stores.handler.ItemDiscountCurrentService;
import com.kosuri.stores.model.dto.ResponseGetItemDiscountCurrentDto;

@RestController
@RequestMapping("/item-discount")
public class ItemDiscountCurrentController {

    @Autowired
    private ItemDiscountCurrentService itemDiscountCurrentService;

    @PostMapping("/add")
    public ItemDiscountCurrent addItemDiscountCurrent(@RequestBody ItemDiscountCurrent itemDiscountCurrentRequest) {
        return itemDiscountCurrentService.createItemDiscountCurrent(itemDiscountCurrentRequest);
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateItemDiscount(@RequestBody ItemDiscountCurrent itemDiscountCurrentRequest) {
        itemDiscountCurrentService.updateItemDiscount(itemDiscountCurrentRequest);
        return ResponseEntity.ok("Discount updated successfully and history saved.");
    }
    
    @GetMapping("/get")
	public ResponseEntity<?> getItemDiscount(@RequestParam(required = false) String userIdStoreIdItemCode,
			@RequestParam(required = false) String itemCode,
			@RequestParam(required = false) Integer discount,
			@RequestParam(required = false) String updatedBy,
			@RequestParam(required = false) Map<String, String> requestParams) {

    	ResponseGetItemDiscountCurrentDto response = new ResponseGetItemDiscountCurrentDto();

		try {
			List<ItemDiscountCurrent> itemDiscountCurrent = itemDiscountCurrentService.getItemDiscount(userIdStoreIdItemCode,itemCode,discount,updatedBy, requestParams);
			if (itemDiscountCurrent != null || itemDiscountCurrent.isEmpty()) {
				response.setMessage("Item Discount Current details");
				response.setStatus(true);
				response.setData(itemDiscountCurrent);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("No details found for given parameters/check your parameters");
				response.setStatus(false);
				response.setData(itemDiscountCurrent);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());

		}
	}
    
}