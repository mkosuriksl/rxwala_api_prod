package com.kosuri.stores.controller;

import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.DiagnosticHandler;
import com.kosuri.stores.model.request.DiagnosticCenterRequestDto;
import com.kosuri.stores.model.request.UpdateDiagnosticCenterRequestDto;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.GetAllDiagnosticCentersResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diagnosticCenter")
public class DiagnosticCenterController {

	@Autowired
	private DiagnosticHandler diagnosticHandler;

	@PostMapping("/addDiagnosticCenter")
	public ResponseEntity<GenericResponse> addUser(@RequestBody DiagnosticCenterRequestDto request) {
		HttpStatus httpStatus;
		GenericResponse response = new GenericResponse();
		try {
			response = diagnosticHandler.addDiagnosticCenter(request);
			httpStatus = HttpStatus.OK;
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

	@PutMapping("/updateDiagnosticCenter")
	public ResponseEntity<GenericResponse> updateDiagnosticCenter(
			@RequestBody UpdateDiagnosticCenterRequestDto requests) {
		HttpStatus httpStatus;
		GenericResponse response = new GenericResponse();
		boolean isDcUpdated = false;
		try {
			isDcUpdated = diagnosticHandler.updateDiagnosticCenter(requests);
			httpStatus = HttpStatus.OK;
			if (isDcUpdated) {
				response.setResponseMessage("Diagnostic Center updated successfully");
			} else {
				response.setResponseMessage("Diagnostic Center Cannot Be Updated");
			}

		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

	@GetMapping("/getAllDiagnosticCenters")
	public ResponseEntity<GetAllDiagnosticCentersResponse> getAllDiagnosticCenters(
			@RequestParam(required = false) String storeId, @RequestParam(required = false) String serviceId) {
		HttpStatus httpStatus;
		GetAllDiagnosticCentersResponse response = new GetAllDiagnosticCentersResponse();

		try {
			response = diagnosticHandler.getAllDiagnosticCenters(storeId, serviceId);
			httpStatus = HttpStatus.OK;

		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

	@GetMapping("/getDiagnosticCenter")
	public ResponseEntity<GenericResponse> searchDiagnosticCenter(
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "serviceId", required = false) String serviceId,
			@RequestParam(value = "storeId", required = false) String storeId) {
		HttpStatus httpStatus;
		GetAllDiagnosticCentersResponse response = new GetAllDiagnosticCentersResponse();

		try {
			response = diagnosticHandler.getDiagnosticCenterByLocationOrUserIdOrStoreId(location, serviceId, storeId);
			httpStatus = HttpStatus.OK;

		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

}
