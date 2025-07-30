package com.kosuri.stores.controller;

import java.util.List;
import java.util.Map;

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

import com.kosuri.stores.handler.SalePharmaHandler;
import com.kosuri.stores.model.request.SalePharmaRequest;
import com.kosuri.stores.model.response.SalePharmaResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sale-pharma")
public class SalePharmaDetailController {

	@Autowired
	private SalePharmaHandler salePharmaHandler;

	@GetMapping("/getAll")
	public ResponseEntity<List<SalePharmaResponse>> getSalePharmaDetails() {
		log.info(">>Controller Logger getSalePharmaDetails({})");
		return ResponseEntity.status(HttpStatus.OK).body(salePharmaHandler.getSalePharmaDetails());
	}

	@GetMapping("/{pharmaPurchaseId}")
	public ResponseEntity<SalePharmaResponse> getSalePharmaById(@PathVariable String pharmaPurchaseId) {
		log.info(">>Controller Logger getPharmaPurchaseById({})", pharmaPurchaseId);
		return ResponseEntity.status(HttpStatus.OK).body(salePharmaHandler.getSalePharmaById(pharmaPurchaseId));
	}

	@PostMapping("/add")
	public ResponseEntity<Map<String, Object>> saveSalePharmaDetail(
			@Valid @RequestBody SalePharmaRequest salePharmaRequest, @RequestParam("email_id") String emailId) {
		log.info(">>  saveSalePharmaDetail({})", salePharmaRequest,emailId);
		return ResponseEntity.ok(salePharmaHandler.saveSalePharmaDetail(salePharmaRequest,emailId));
	}

}
