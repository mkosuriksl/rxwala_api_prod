package com.kosuri.stores.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.CustomerOrderDetailsEntity;
import com.kosuri.stores.dao.DistributorRetailerOrderDetailsEntity;
import com.kosuri.stores.handler.CustomerOrderMethodHandler;
import com.kosuri.stores.model.dto.ApiResponse;
import com.kosuri.stores.model.dto.CustomerGetOrderResponseDTO;
import com.kosuri.stores.model.dto.CustomerOrderRequestDto;
import com.kosuri.stores.model.dto.DistributorRetailerOrderResponseDTO;
import com.kosuri.stores.model.dto.UpdateCustomerOrderRequestDto;
import com.kosuri.stores.model.dto.UpdateRetailerOrderRequestDto;

import jakarta.persistence.EntityNotFoundException;
@RestController
@RequestMapping("/customer-cart")
public class CustomerOrderMethodController {

    @Autowired
    private CustomerOrderMethodHandler orderMethodHandler ;


    @PostMapping("/add")
    public ResponseEntity<?> createCustomerOrderMethod(@RequestBody CustomerOrderRequestDto requestDto) {
        try {
        	orderMethodHandler.ceateCustomerOrderMethod(requestDto);
            return ResponseEntity.ok("Customer Order Method placed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to place order: " + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateOrderLine(@RequestBody UpdateCustomerOrderRequestDto dto) {
        try {
            List<CustomerOrderDetailsEntity> updated = orderMethodHandler.updateOrderDetails(dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/get")
    public ApiResponse<CustomerGetOrderResponseDTO> getOrders(
    		 @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String orderlineId,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String manufacturerName,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String userIdStoreIdItemCode,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return orderMethodHandler.getOrderDetail(
        		orderId,
                orderlineId,
                itemName,
                manufacturerName,
                itemCode,
                storeId,
                userIdStoreIdItemCode,
                fromDate,
                toDate,
                userId,
                pageable
        );
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteOrder(@RequestParam String orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            String message = orderMethodHandler.deleteOrderByOrderId(orderId);
            response.put("status", true);
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("status", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Error deleting order.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
