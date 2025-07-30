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

import com.kosuri.stores.handler.PharmaPurchaseHandler;
import com.kosuri.stores.model.request.PharmaPurchaseRequest;
import com.kosuri.stores.model.response.PharmaPurchaseResponse;
import com.kosuri.stores.utils.CurrentUser;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/pharma-purchase")
public class PharmaPurchaseDetailController {

	@Autowired
	private PharmaPurchaseHandler pharmaPurchaseHandler;

	@GetMapping("/getAll")
	public ResponseEntity<List<PharmaPurchaseResponse>> getPharmaPurchases() {
		log.info(">>Controller Logger getPharmaPurchases({})");
		String email = CurrentUser.getEmail();
		return ResponseEntity.status(HttpStatus.OK).body(pharmaPurchaseHandler.getPharmaPurchases(email));
	}

	@GetMapping("/{pharmaPurchaseId}")
	public ResponseEntity<PharmaPurchaseResponse> getPharmaPurchaseById(@PathVariable String pharmaPurchaseId) {
		log.info(">>Controller Logger getPharmaPurchaseById({})", pharmaPurchaseId);
		String email = CurrentUser.getEmail();
		return ResponseEntity.status(HttpStatus.OK).body(pharmaPurchaseHandler.getPharmaPurchaseById(pharmaPurchaseId,email));
	}

	@PostMapping("/add")
	public ResponseEntity<Map<String, Object>> savePharmaPurchaseDetail(
			@Valid @RequestBody PharmaPurchaseRequest pharmaPurchaseRequest, @RequestParam("email_id") String emailId) {
		log.info(">>  savePharmaPurchaseDetail({})", pharmaPurchaseRequest,emailId);
		return ResponseEntity.ok(pharmaPurchaseHandler.savePharmaPurchaseDetail(pharmaPurchaseRequest,emailId));
	}

}
