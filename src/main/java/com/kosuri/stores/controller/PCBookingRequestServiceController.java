package com.kosuri.stores.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.handler.DCBookingRequestService;
import com.kosuri.stores.model.dto.AppintmentBookingPCUpdateResponse;
import com.kosuri.stores.model.dto.AppintmentBookingRequest;
import com.kosuri.stores.model.dto.CancelLineStatusRequest;
import com.kosuri.stores.model.dto.CancelLineStatusResponse;
import com.kosuri.stores.model.dto.DCBookingGetDto;
import com.kosuri.stores.model.dto.DCBookingReponseDto;
import com.kosuri.stores.model.dto.DCBookingRequestDto;
import com.kosuri.stores.model.dto.DCBookingStoreGetDto;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pc-bookingRequest-customer")
public class PCBookingRequestServiceController {

	@Autowired
	private DCBookingRequestService dcBookingRequestService;

	@PostMapping("/add")
	public DCBookingReponseDto addBookingRequestDetails(@RequestBody DCBookingRequestDto bookingRequestDetailsList) {
		return dcBookingRequestService.createBookingRequestDetails(bookingRequestDetailsList);
	}

	@PutMapping("/update")
	public ResponseEntity<DCBookingRequestDto> updateBookingRequestDetails(
			@RequestBody DCBookingRequestDto bookingRequestDetailsList) {
		DCBookingRequestDto updatedPackage = dcBookingRequestService
				.updateBookingRequestDetails(bookingRequestDetailsList);
		return ResponseEntity.ok(updatedPackage);
	}

	@GetMapping("/getCustomerBookingsWithCustomerInfo")
	public ResponseEntity<List<DCBookingGetDto>> getBookingsWithDetails(
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate bookingDateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate bookingDateTo,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate appointmentDateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate appointmentDateTo,
			@RequestParam(required = false) String storeUserContact,HttpServletRequest request) { // Add this parameter

		validateUnexpectedParams(request, Set.of("userId", "bookingDateFrom", "bookingDateTo", "appointmentDateFrom", "appointmentDateTo"));
		List<DCBookingGetDto> bookings = dcBookingRequestService.getCustomerBookingsWithCustomerDetails(userId,
				bookingDateFrom, bookingDateTo, appointmentDateFrom, appointmentDateTo, storeUserContact);

		return ResponseEntity.ok(bookings);
	}
	private void validateUnexpectedParams(HttpServletRequest request, Set<String> allowedParams) {
	    Map<String, String[]> paramMap = request.getParameterMap();
	    for (String param : paramMap.keySet()) {
	        if (!allowedParams.contains(param)) {
	            throw new ResourceNotFoundException("Unexpected parameter: " + param);
	        }
	    }
	}
	@GetMapping("/getCustomerBookingsWithStoreInfo")
	public ResponseEntity<List<DCBookingStoreGetDto>> getCustomerBookingsWithStore(
			@RequestParam(required = false) String customerId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate bookingDateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate bookingDateTo,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate appointmentDateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate appointmentDateTo,HttpServletRequest request) { 
		validateUnexpectedParams(request, Set.of("customerId", "bookingDateFrom", "bookingDateTo", "appointmentDateFrom", "appointmentDateTo"));
		List<DCBookingStoreGetDto> bookings = dcBookingRequestService.getCustomerBookingsWithStore(customerId,
				bookingDateFrom, bookingDateTo, appointmentDateFrom, appointmentDateTo);

		return ResponseEntity.ok(bookings);
	}
	
//	@PostMapping("/cancelLineStatus")
//	public ResponseEntity<Map<String, Object>> cancelLineItem(@RequestParam String serviceRequestId,
//			@RequestParam String serviceRequestIdLineId) {
//		String message = dcBookingRequestService.cancelLineItemStatus(serviceRequestId, serviceRequestIdLineId);
//		boolean success = message.contains("successfully marked as CANCELLED");
//		Map<String, Object> response = new HashMap<>();
//		response.put("status", success ? "SUCCESS" : "FAILED");
//		response.put("message", message);
//
//		return ResponseEntity.ok(response);
//	}
	
//	@PostMapping("/cancelLineStatus")
//	public ResponseEntity<String> cancelLineStatus(@RequestParam String serviceRequestId,
//			@RequestParam String serviceRequestLineId) {
//
//		dcBookingRequestService.cancelLineItem(serviceRequestId, serviceRequestLineId);
//
//		return ResponseEntity.ok("Line item canceled successfully. Header status updated if all items are canceled.");
//	}
	
	@PostMapping("/cancelLineStatus")
	public ResponseEntity<CancelLineStatusResponse> cancelLineStatus(@RequestBody CancelLineStatusRequest request) {
	    CancelLineStatusResponse response = dcBookingRequestService.cancelLineItems(request);
	    return ResponseEntity.ok(response);
	}


	@PutMapping("/updateAppointment")
	public ResponseEntity<AppintmentBookingPCUpdateResponse> updateAppointment(
			@RequestBody AppintmentBookingRequest bookingRequestDetailsList) {
		AppintmentBookingPCUpdateResponse updatedPackage = dcBookingRequestService
				.updateAppointment(bookingRequestDetailsList);
		return ResponseEntity.ok(updatedPackage);
	}
}