package com.kosuri.stores.controller;

import com.kosuri.stores.dao.CustomerRetailerOrderHdrEntity;
import com.kosuri.stores.handler.CustomerOrderHandler;
import com.kosuri.stores.handler.OrderUpdatedHandler;
import com.kosuri.stores.model.dto.GenericResponse;
import com.kosuri.stores.model.dto.OrderDetailsCustomerDto;
import com.kosuri.stores.model.dto.OrderQtyUpdateDto;
import com.kosuri.stores.model.dto.OrderRequestDto;
import com.kosuri.stores.model.dto.OrderUpdatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CustomerOrderPlaceController {

    @Autowired
    private CustomerOrderHandler customerOrderHandler;
    @Autowired
    private OrderUpdatedHandler orderUpdatedHandler;

    @PostMapping("/placeOrder")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequestDto orderRequestDto) {
            CustomerRetailerOrderHdrEntity orderHdr = customerOrderHandler.placeOrder(orderRequestDto);
            return ResponseEntity.ok(String.valueOf(orderHdr));

    }

    @GetMapping("/order/details")
    public ResponseEntity<List<OrderDetailsCustomerDto>>getOrderDetailsAndCustomer(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String customerNumber,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws InstantiationException, IllegalAccessException {

        return ResponseEntity .ok(customerOrderHandler.getOrderDetailsAndCustomerByParams(orderId, location, customerNumber, customerEmail,
        		fromDate,toDate));
    }

    @PostMapping("/update/{orderId}")
    public ResponseEntity<String> saveOrderUpdate(@PathVariable String orderId,
                                                  @RequestBody OrderUpdatedDto updated) {
        try {
            orderUpdatedHandler.saveOrderUpdate(orderId,updated);
            return ResponseEntity.ok("Order update saved successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update-order-qty")
    public ResponseEntity<GenericResponse<OrderQtyUpdateDto>> updateOrderQty(@RequestBody OrderQtyUpdateDto dto) {
        try {
        	customerOrderHandler.updateOrderQty(dto); // method still returns void or String
            GenericResponse<OrderQtyUpdateDto> response = new GenericResponse<>("true", "Updated successfully", dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            GenericResponse<OrderQtyUpdateDto> response = new GenericResponse<>("false", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            GenericResponse<OrderQtyUpdateDto> response = new GenericResponse<>("false", "Something went wrong", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
