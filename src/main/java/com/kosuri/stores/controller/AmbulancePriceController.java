package com.kosuri.stores.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.handler.AmbulancePriceHandler;
import com.kosuri.stores.model.request.AmbulancePriceRequest;
import com.kosuri.stores.model.response.AmbulancePriceResponse;
import com.kosuri.stores.model.response.GenericResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ambulance-price")
public class AmbulancePriceController {

	@Autowired
	private AmbulancePriceHandler ambulancePriceHandler;

	@GetMapping("/getAll")
	public ResponseEntity<List<AmbulancePriceResponse>> getAmbulancePrices(@RequestParam(required = false) Long priceId,
			@RequestParam(required = false) String ambulanceRegNo, @RequestParam(required = false) Double pricePerKm,
			@RequestParam(required = false) String updateSrcLocation,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updateDate,
			@RequestParam(required = false) Double waitingCharges, @RequestParam(required = false) String driverLicNo,
			@RequestParam(required = false) String driverName, @RequestParam(required = false) Boolean active,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdOn,
			@RequestParam(required = false) String createdBy,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedOn,
			@RequestParam(required = false) String updatedBy, @RequestParam(required = false) String contactNumber) {
		log.info(">>Controller Logger getAmbulancePrices({})",priceId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(ambulancePriceHandler.getAmbulancePrices(priceId, ambulanceRegNo, pricePerKm, updateSrcLocation,
						updateDate, waitingCharges, driverLicNo, driverName, active, createdOn, createdBy, updatedOn,
						updatedBy, contactNumber));
	}

	@GetMapping("/{ambulancePriceId}")
	public ResponseEntity<AmbulancePriceResponse> getAmbulancePriceById(@PathVariable String ambulancePriceId) {
		log.info(">>Controller Logger getAmbulancePriceById({})");
		return ResponseEntity.status(HttpStatus.OK).body(ambulancePriceHandler.getAmbulancePriceById(ambulancePriceId));
	}

	@PostMapping("/add")
	public ResponseEntity<GenericResponse> saveAmbulancePrice(
			@Valid @RequestBody AmbulancePriceRequest ambulancePriceRequest) {
		log.info(">>Controller Logger save Ambulance Price({})");
		GenericResponse response = new GenericResponse();
		try {
			ambulancePriceHandler.saveAmbulancePrice(ambulancePriceRequest);
			response.setResponseMessage("Ambulance Price created successfully!");
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}
