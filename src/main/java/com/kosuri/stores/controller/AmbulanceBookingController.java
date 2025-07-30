package com.kosuri.stores.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.handler.AmbulanceBookingHandler;
import com.kosuri.stores.model.request.AmbulanceBookingDetailRequest;
import com.kosuri.stores.model.response.AmbulanceBookingDetailResponse;
import com.kosuri.stores.model.response.GenericResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ambulance-booking")
public class AmbulanceBookingController {

	@Autowired
	private AmbulanceBookingHandler ambulanceBookingHandler;

	@GetMapping("/getAll")
	public ResponseEntity<List<AmbulanceBookingDetailResponse>> getAmbulances(
			@RequestParam(required = false) String bookingNo, @RequestParam(required = false) LocalDateTime bookingDate,
			@RequestParam(required = false) String patientName, @RequestParam(required = false) String fromLocation,
			@RequestParam(required = false) String toLocation, @RequestParam(required = false) String customerContNum,
			@RequestParam(required = false) String contactPerson, @RequestParam(required = false) String bookedBy,
			@RequestParam(required = false) String status, @RequestParam(required = false) String remarks,
			@RequestParam(required = false) Boolean active, @RequestParam(required = false) LocalDateTime createdOn,
			@RequestParam(required = false) String createdBy, @RequestParam(required = false) LocalDateTime updatedOn,
			@RequestParam(required = false) String updatedBy, @RequestParam(required = false) String ambulanceRegNo) {
		log.info(">>Controller Logger getAmbulances({})");
		return ResponseEntity.status(HttpStatus.OK)
				.body(ambulanceBookingHandler.getAmbulanceBookings(bookingNo, bookingDate, patientName, fromLocation,
						toLocation, customerContNum, contactPerson, bookedBy, status, remarks, active, createdOn,
						createdBy, updatedOn, updatedBy, ambulanceRegNo));
	}

	@GetMapping("/{ambulanceBookingId}")
	public ResponseEntity<AmbulanceBookingDetailResponse> getAmbulanceById(@PathVariable String ambulanceBookingId) {
		log.info(">>Controller Logger getAmbulanceById({})");
		return ResponseEntity.status(HttpStatus.OK)
				.body(ambulanceBookingHandler.getAmbulanceBookingById(ambulanceBookingId));
	}

	@PostMapping("/add")
	public ResponseEntity<GenericResponse> saveAmbulanceBooking(
			@Valid @RequestBody AmbulanceBookingDetailRequest request) {
		log.info(">>Controller Logger save Ambulance Booking({})", request);
		GenericResponse response = new GenericResponse();
		try {
			ambulanceBookingHandler.saveAmbulanceBooking(request);
			response.setResponseMessage("Ambulance Booking created successfully!");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}
