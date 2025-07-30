package com.kosuri.stores.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.handler.BookingService;
import com.kosuri.stores.model.dto.AppintmentBookingRequest;
import com.kosuri.stores.model.dto.AppintmentBookingRequestdc;
import com.kosuri.stores.model.dto.AppintmentBookingResponse;
import com.kosuri.stores.model.dto.BookingRequest;
import com.kosuri.stores.model.dto.BookingResponse;
import com.kosuri.stores.model.dto.CancelLineStatusRequest;
import com.kosuri.stores.model.dto.CancelLineStatusResponse;
import com.kosuri.stores.model.dto.GetBookingResponse;
import com.kosuri.stores.model.dto.GetBookingResponseCustomerInfo;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dc-bookingRequest")
public class BookingController {

	@Autowired
    private BookingService bookingService; 
	
	@PostMapping("/add")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest bookingRequest) {
        try {
            BookingResponse bookingResponse = bookingService.createBooking(bookingRequest);
            return new ResponseEntity<>(bookingResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@PutMapping("/update")
    public ResponseEntity<AppintmentBookingResponse> updateAppointment(@RequestBody AppintmentBookingRequestdc request) {
        AppintmentBookingResponse response = bookingService.updateAppointment(request);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/getCustomerBookingsWithStoreInfo")
	public ResponseEntity<List<GetBookingResponse>> getCustomerBookingsWithStore(
			@RequestParam(required = false) String customerId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate bookingDateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate bookingDateTo,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate appointmentDateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate appointmentDateTo,HttpServletRequest request) { 
		validateUnexpectedParams(request, Set.of("customerId", "bookingDateFrom", "bookingDateTo", "appointmentDateFrom", "appointmentDateTo"));
		List<GetBookingResponse> bookings = bookingService.getCustomerBookingsWithStore(customerId,
				bookingDateFrom, bookingDateTo, appointmentDateFrom, appointmentDateTo);

		return ResponseEntity.ok(bookings);
	}
	
	@GetMapping("/getCustomerBookingsWithCustomerInfo")
	public ResponseEntity<List<GetBookingResponseCustomerInfo>> getCustomerBookingsWithCustomer(
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate bookingDateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate bookingDateTo,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate appointmentDateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate appointmentDateTo,HttpServletRequest request) { 
		validateUnexpectedParams(request, Set.of("userId", "bookingDateFrom", "bookingDateTo", "appointmentDateFrom", "appointmentDateTo"));
		List<GetBookingResponseCustomerInfo> bookings = bookingService.getCustomerBookingsWithCustomer(userId,
				bookingDateFrom, bookingDateTo, appointmentDateFrom, appointmentDateTo);

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

	@PostMapping("/cancelLineStatus")
	public ResponseEntity<CancelLineStatusResponse> cancelBookingLines(@RequestBody CancelLineStatusRequest request) {
	    CancelLineStatusResponse response = bookingService.cancelServiceRequestLines(request);
	    return ResponseEntity.ok(response);
	}
//	public ResponseEntity<String> cancelLineStatus(@RequestParam String serviceRequestId,
//			@RequestParam String serviceRequestLineId) {
//
//		bookingService.cancelLineItem(serviceRequestId, serviceRequestLineId);
//
//		return ResponseEntity.ok("Line item canceled successfully. Header status updated if all items are canceled.");
//	}
	
//	@PostMapping("/cancelLineStatus")
//	public ResponseEntity<Map<String, Object>> cancelLineItem(@RequestParam String serviceRequestId,
//			@RequestParam String serviceRequestIdLineId) {
//		String message = bookingService.cancelLineItemStatus(serviceRequestId, serviceRequestIdLineId);
//		boolean success = message.contains("successfully marked as CANCELLED");
//		Map<String, Object> response = new HashMap<>();
//		response.put("status", success ? "SUCCESS" : "FAILED");
//		response.put("message", message);
//
//		return ResponseEntity.ok(response);
//	}
}
