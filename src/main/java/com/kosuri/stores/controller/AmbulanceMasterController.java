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

import com.kosuri.stores.handler.AmbulanceMasterHandler;
import com.kosuri.stores.model.request.AmbulanceMasterRequest;
import com.kosuri.stores.model.response.AmbulanceMasterResponse;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.utils.CurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ambulance-master")
public class AmbulanceMasterController {

	@Autowired
	private AmbulanceMasterHandler ambulanceMasterHandler;

	@GetMapping("/getAll")
	public ResponseEntity<List<AmbulanceMasterResponse>> getAmbulanceMasters(
			@RequestParam(required = false) String ambulanceRegNo, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String baseLocation,
			@RequestParam(required = false) String vehicleBrand, @RequestParam(required = false) String vehicleModel,
			@RequestParam(required = false) String rtoRegLocation, @RequestParam(required = false) String state,
			@RequestParam(required = false) String vin, @RequestParam(required = false) String ownerName,
			@RequestParam(required = false) String rtoDoc, @RequestParam(required = false) String insuDoc,
			@RequestParam(required = false) String ambLicDoc, @RequestParam(required = false) Boolean ventilator,
			@RequestParam(required = false) String primaryCareNurse,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime regDate,
			 @RequestParam(required = false) String image,
			@RequestParam(required = false) String additionalFeatures, @RequestParam(required = false) Boolean verify,
			@RequestParam(required = false) Boolean active, @RequestParam(required = false) String verifiedBy,
			@RequestParam(required = false) String updatedby) {
		log.info(">>Controller Logger getAmbulanceMasters({})");
		return ResponseEntity.status(HttpStatus.OK)
				.body(ambulanceMasterHandler.getAmbulanceMasters(ambulanceRegNo, userId, phoneNumber, baseLocation,
						vehicleBrand, vehicleModel, rtoRegLocation, state, vin, ownerName, rtoDoc, insuDoc, ambLicDoc,
						ventilator, primaryCareNurse, regDate,  image, additionalFeatures, verify, active,
						verifiedBy, updatedby));
	}

	@GetMapping("/{ambulanceMasterId}")
	public ResponseEntity<AmbulanceMasterResponse> getAmbulanceMasterById(@PathVariable String ambulanceMasterId) {
		log.info(">>Controller Logger getAmbulanceById({})", ambulanceMasterId);
		String email = CurrentUser.getEmail();
		System.out.println("Logged User Email====> "+email);
		return ResponseEntity.status(HttpStatus.OK)
				.body(ambulanceMasterHandler.getAmbulanceMasterById(ambulanceMasterId));
	}

	@PostMapping("/add")
	public ResponseEntity<GenericResponse> saveAmbulanceMaster(
			@RequestBody AmbulanceMasterRequest ambulanceMasterRequest) {
		log.info(">>Controller Logger save AmbulanceMaster({})", ambulanceMasterRequest);
		String email = CurrentUser.getEmail();
		GenericResponse response = new GenericResponse();
		try {
			ambulanceMasterHandler.saveAmbulanceMaster(email,ambulanceMasterRequest);
			response.setResponseMessage("Ambulance Master created successfully!");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}
