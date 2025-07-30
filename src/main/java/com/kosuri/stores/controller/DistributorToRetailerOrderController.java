package com.kosuri.stores.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.DistributorRetailerOrderDetailsEntity;
import com.kosuri.stores.dao.DistributorRetailerOrderHdrEntity;
import com.kosuri.stores.handler.DistributorToRetailerHandler;
import com.kosuri.stores.model.dto.ApiResponse;
import com.kosuri.stores.model.dto.DistributorRetailerOrderHdrEnrichedDto;
import com.kosuri.stores.model.dto.DistributorRetailerOrderResponseDTO;
import com.kosuri.stores.model.dto.ResponsesGetDistributorRetailerOrderHdrDataDto;
import com.kosuri.stores.model.dto.RetailerOrderRequestDto;
import com.kosuri.stores.model.dto.UpdateRetailerOrderRequestDto;
@RestController
public class DistributorToRetailerOrderController {

    @Autowired
    private DistributorToRetailerHandler distributorToRetailerHandler;


    @PostMapping("/RetailerToDistributorOrderPlace")
    public ResponseEntity<?> placeOrder(@RequestBody RetailerOrderRequestDto requestDto) {
        try {
            distributorToRetailerHandler.placeOrder(requestDto);
            return ResponseEntity.ok("Order placed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to place order: " + e.getMessage());
        }
    }

    @PutMapping("/update-RetailerToDistributorOrderPlace")
    public ResponseEntity<?> updateOrderLine(@RequestBody UpdateRetailerOrderRequestDto dto) {
        try {
            List<DistributorRetailerOrderDetailsEntity> updated = distributorToRetailerHandler.updateOrderDetails(dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/get-RetailerToDistributorOrderPlace")
    public ApiResponse<DistributorRetailerOrderResponseDTO> getOrders(
    		 @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String orderlineId,
            @RequestParam(required = false) String retailerId,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String itemCategory,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String manufacturerName,
            @RequestParam(required = false) String distributorId,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String userIdStoreIdItemCode,
            @RequestParam(required = false) String invoiceNo,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return distributorToRetailerHandler.getDistributorRetailerOrderDetail(
        		orderId,
                orderlineId,
                retailerId,
                itemName,
                itemCategory,
                brandName,
                manufacturerName,
                distributorId,
                itemCode,
                storeId,
                userIdStoreIdItemCode,
                invoiceNo,
                fromDate,
                toDate,
                pageable
        );
    }

    @GetMapping("/getOrderHeaderDetails")
	public ResponseEntity<ResponsesGetDistributorRetailerOrderHdrDataDto> getWorkers(
	        @RequestParam(required = false) String orderId,
	        @RequestParam(required = false) String retailerId,
	        @RequestParam(required = false) String distrubutorId,
	        @RequestParam(required = false) String storeId,
	        @RequestParam(required = false) String invoiceNo,
	        @RequestParam(required = false) String fromDate,
	        @RequestParam(required = false)String toDate,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    Pageable pageable = PageRequest.of(page, size);
	    Page<DistributorRetailerOrderHdrEnrichedDto> pages = distributorToRetailerHandler.getDistributorRetailerOrderHdr(orderId, retailerId, distrubutorId, storeId, invoiceNo,fromDate,toDate, pageable);

	    ResponsesGetDistributorRetailerOrderHdrDataDto response = new ResponsesGetDistributorRetailerOrderHdrDataDto();
	    response.setMessage("Distributor Retailer Order Header retrieved successfully.");
	    response.setStatus(true);
	    response.setDistributorRetailerOrderHdrData(pages.getContent());

	    // Set pagination fields
	    response.setCurrentPage(pages.getNumber());
	    response.setPageSize(pages.getSize());
	    response.setTotalElements(pages.getTotalElements());
	    response.setTotalPages(pages.getTotalPages());

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
//	public ResponseEntity<ResponseGetDistributorRetailerOrderDetailsEntityDto> getServiceRequestCarpentaryQuotation(
//			@RequestParam(required = false) String orderlineId,
//			@RequestParam(required = false) String retailerId,
//			@RequestParam(required = false) String itemName, @RequestParam(required = false) String itemCategory,
//			@RequestParam(required = false) String brandName, @RequestParam(required = false) String manufacturerName,
//			@RequestParam(required = false) String distributorId, @RequestParam(required = false) String itemCode,
//			@RequestParam(required = false) String storeId, @RequestParam(required = false) String userIdStoreIdItemCode,
//			@RequestParam(required = false) String invoiceNo, @RequestParam(required = false)@DateTimeFormat String fromDate,
//	        @RequestParam(required = false)@DateTimeFormat String toDate,
//			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
//
//		Pageable pageable = PageRequest.of(page, size);
//		Page<DistributorRetailerOrderDetailsEntity> srpqPage = distributorToRetailerHandler
//				.getDistributorRetailerOrderDetails( orderlineId, retailerId,
//						itemName, itemCategory, brandName, manufacturerName,
//						distributorId,itemCode,storeId,userIdStoreIdItemCode,invoiceNo,fromDate,toDate,pageable);
//		ResponseGetDistributorRetailerOrderDetailsEntityDto response = new ResponseGetDistributorRetailerOrderDetailsEntityDto();
//
//		response.setMessage("Distributor Retailer Order details retrieved successfully.");
//		response.setStatus(true);
//		response.setDistributorRetailerOrderDetailsEntity(srpqPage.getContent());
//
//		// Set pagination fields
//		response.setCurrentPage(srpqPage.getNumber());
//		response.setPageSize(srpqPage.getSize());
//		response.setTotalElements(srpqPage.getTotalElements());
//		response.setTotalPages(srpqPage.getTotalPages());
//
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}
}
