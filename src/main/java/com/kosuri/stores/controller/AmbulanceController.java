package com.kosuri.stores.controller;

import java.time.LocalDate;

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

import com.kosuri.stores.handler.AmbulanceService;
import com.kosuri.stores.model.dto.AmbulanceModel;
import com.kosuri.stores.model.dto.ResponseModel;

@RestController
@RequestMapping("/api/ambulance/")
public class AmbulanceController {

	@Autowired
	AmbulanceService ambulanceService;

	@PostMapping("add-ambulance")
	public ResponseEntity<?> addAmbulance(@RequestBody AmbulanceModel request) {
		ResponseModel response = ambulanceService.addAmbulance(request);
		if (response.getError().equals("false")) {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("update-ambulance")
	public ResponseEntity<?> updateAmbulanceDetail(@RequestBody AmbulanceModel request) {
		return ambulanceService.updateAmbulanceDetail(request);
	}
	
	@GetMapping("get-ambulances")
	public ResponseEntity<?> getAmbulances(
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "model", required = false) String model,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {
		return ambulanceService.getAmbulances(brand, model, startDate, endDate);
	}
	
}
