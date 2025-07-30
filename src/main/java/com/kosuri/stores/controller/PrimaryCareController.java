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

import com.kosuri.stores.dao.PrimaryCareEntity;
import com.kosuri.stores.handler.PrimaryCareHandler;
import com.kosuri.stores.model.dto.PrimaryCareDTOWithoutSec;
import com.kosuri.stores.model.request.PrimaryCareServiceRequest;
import com.kosuri.stores.model.request.PrimaryCareUserRequest;
import com.kosuri.stores.model.request.UpdatePrimaryCareServicesRequest;
import com.kosuri.stores.model.request.UpdatePrimaryCareServicesResponse;
import com.kosuri.stores.model.request.UpdatePrimaryCareUserRequest;
import com.kosuri.stores.model.request.UpdateServicesRequest;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.GetAllPrimaryCareCentersResponse;
import com.kosuri.stores.utils.CurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/primaryCare")
public class PrimaryCareController {
	@Autowired
	PrimaryCareHandler primaryCareHandler;

	@PostMapping("/addPrimaryCareCenter")
	public ResponseEntity<GenericResponse> addPrimaryCare(@RequestBody List<PrimaryCareUserRequest> requests)
			throws Exception {
		String loggedInUserEmail = CurrentUser.getEmail();
		log.info(">> addPrimaryCare({}, {})", loggedInUserEmail, requests);
		return new ResponseEntity<>(primaryCareHandler.addPrimaryCare(requests, loggedInUserEmail), HttpStatus.OK);
	}

	@PostMapping("/service")
	public ResponseEntity<GenericResponse> addPrimaryCareServices(
			@RequestBody PrimaryCareServiceRequest serviceRequest) {
		String loggedInUserEmail = CurrentUser.getEmail();
		log.info(">> addPrimaryCare({}, {})", loggedInUserEmail, serviceRequest);
		return new ResponseEntity<>(primaryCareHandler.addPrimaryCareServices(serviceRequest, loggedInUserEmail),
				HttpStatus.OK);
	}

	@GetMapping("/service")
	public ResponseEntity<GenericResponse> getPrimaryCareServices(@RequestParam(required = false) String storeId,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String location) throws Exception {
		String loggedInUserEmail = CurrentUser.getEmail();
		log.info(">> addPrimaryCare({}, {})", loggedInUserEmail);
		return new ResponseEntity<>(primaryCareHandler.getPrimaryCareServices(loggedInUserEmail,storeId,serviceId,location), HttpStatus.OK);
	}
	
	@GetMapping("/service-WithoutSec")
	public ResponseEntity<GenericResponse> getPrimaryCareServices(@RequestParam(required = false) String userIdStoreId,
			@RequestParam(required = false) String userId, @RequestParam(required = false) String serviceId,
			@RequestParam Map<String, String> requestParams) {

		try {
			// Call the service method to fetch data
			List<PrimaryCareDTOWithoutSec> primaryCareServices = primaryCareHandler.getPrimaryCareServicesWithoutSec(userIdStoreId,
					userId, serviceId, requestParams);

			// Create a GenericResponse object
			GenericResponse response = new GenericResponse();
			response.setResponseMessage("Primary care services retrieved successfully.");
			response.setDetails(primaryCareServices);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			// Handle invalid parameters
			GenericResponse errorResponse = new GenericResponse();
			errorResponse.setResponseMessage(e.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			// Handle other exceptions
			GenericResponse errorResponse = new GenericResponse();
			errorResponse.setResponseMessage("An error occurred while fetching primary care services.");
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PutMapping("/update-service")
	public ResponseEntity<UpdatePrimaryCareServicesResponse> updatePrimaryCareCenterServices(
	        @RequestBody UpdateServicesRequest requests) throws Exception {
	    String loggedInUserEmail = CurrentUser.getEmail();
	    log.info(">> updatePrimaryCareCenter({}, {})", loggedInUserEmail, requests);
	    UpdatePrimaryCareServicesResponse response = primaryCareHandler.updatePrimaryCareCenterServices(requests, loggedInUserEmail);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
//	public ResponseEntity<String> updatePrimaryCareCenterServices(
//			@RequestBody UpdatePrimaryCareServicesRequest requests) throws Exception {
//		String loggedInUserEmail = CurrentUser.getEmail();
//		log.info(">> updatePrimaryCareCenter({}, {})", loggedInUserEmail, requests);
//		return new ResponseEntity<>(primaryCareHandler.updatePrimaryCareCenterServices(requests, loggedInUserEmail),
//				HttpStatus.OK);
//	}
//	public ResponseEntity<UpdatePrimaryCareServicesResponse> updatePrimaryCareCenterServices(
//	        @RequestBody UpdatePrimaryCareServicesRequest requests) throws Exception {
//	    String loggedInUserEmail = CurrentUser.getEmail();
//	    log.info(">> updatePrimaryCareCenter({}, {})", loggedInUserEmail, requests);
//	    UpdatePrimaryCareServicesResponse response = primaryCareHandler.updatePrimaryCareCenterServices(requests, loggedInUserEmail);
//	    return new ResponseEntity<>(response, HttpStatus.OK);
//	}

	@PutMapping("/updatePrimaryCareCenter")
	public ResponseEntity<String> updatePrimaryCareCenter(@RequestBody List<UpdatePrimaryCareUserRequest> requests)
			throws Exception {
		String loggedInUserEmail = CurrentUser.getEmail();
		log.info(">> updatePrimaryCareCenter({}, {})", loggedInUserEmail, requests);
		return new ResponseEntity<>(primaryCareHandler.updatePrimaryCareCenter(requests, loggedInUserEmail),
				HttpStatus.OK);
	}

	@GetMapping("/getAllPrimaryCareCenters")
	public ResponseEntity<GetAllPrimaryCareCentersResponse> getAllPrimaryCareCenters() {
		String loggedInUserEmail = CurrentUser.getEmail();
		log.info(">> getAllPrimaryCareCenters({}, {})", loggedInUserEmail);
		return new ResponseEntity<>(primaryCareHandler.getAllPrimaryCareCenters(loggedInUserEmail), HttpStatus.OK);
	}

	@GetMapping("/getPrimaryCareCenter")
	public ResponseEntity<GetAllPrimaryCareCentersResponse> getPrimaryCareCenter(
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "storeId", required = false) String storeId) {
		log.info(">> getPrimaryCareCenter({}, {}, {})", location, userId, storeId);
		HttpStatus httpStatus;
		GetAllPrimaryCareCentersResponse response = new GetAllPrimaryCareCentersResponse();

		try {
			response = primaryCareHandler.getPrimaryCareCenterByLocationOrUserId(location, userId, storeId);
			httpStatus = HttpStatus.OK;

		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}
	
	@GetMapping("/get_pc_service_category_home")
	public List<String> getDcServiceCategoryHomeDistinct() {
		return primaryCareHandler.getPcServiceCategoryHomeDistinct();
	}
	
	 @GetMapping("/get_pc_service_category_home-by-search")
		public ResponseEntity<Map<String, Object>> getServiceCategoryBySearch(@RequestParam(required = false) String serviceCategory) {
			return ResponseEntity.ok(primaryCareHandler.getServiceCategoryBySearch(serviceCategory));
		}
}
