package com.kosuri.stores.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.DiagnosticServicePackageHeader;
import com.kosuri.stores.handler.DiagnosticServicePackageHeaderService;
import com.kosuri.stores.model.dto.DiagnosticServicePackageHeaderRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/diagnostic-service-package-header")
public class DiagnosticServicePackageHeaderController {

	@Autowired
	private DiagnosticServicePackageHeaderService headerService;

	@GetMapping
	public ResponseEntity<List<DiagnosticServicePackageHeader>> getDiagnosticServicePackageHeaders(
			@RequestParam(required = false) String storeId, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String packageId) {
		log.info(">> getDiagnosticServicePackageHeaders()");
		return ResponseEntity.ok(headerService.getDiagnosticServicePackageHeaders(storeId, userId, packageId));
	}

	@GetMapping("/{diagnosticHeaderId}")
	public ResponseEntity<DiagnosticServicePackageHeader> getDiagnosticServicePackageHeaderById(
			@PathVariable String diagnosticHeaderId) {
		log.info(">> getDiagnosticServicePackageHeaderById({})", diagnosticHeaderId);
		return ResponseEntity.ok(headerService.getDiagnosticServicePackageHeaderById(diagnosticHeaderId));
	}

	@PostMapping("/add")
	public ResponseEntity<Void> AddDiagnosticServicePackageHeader(
			@RequestBody DiagnosticServicePackageHeaderRequest req) {
		log.info(">> createDiagnosticServicePackageHeader({})", req);
		headerService.addDiagnosticServicePackageHeaders(req);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{diagnosticHeaderId}")
	public ResponseEntity<Void> updateDiagnosticServicePackageHeader(@PathVariable String diagnosticHeaderId,
			@RequestBody DiagnosticServicePackageHeaderRequest req) {
		log.info(">> updateDiagnosticServicePackageHeader({}, {})", diagnosticHeaderId, req);
		headerService.updateDiagnosticServicePackageHeader(diagnosticHeaderId, req);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/{diagnosticHeaderId}")
	public ResponseEntity<Void> deleteDiagnosticServicePackageHeader(@PathVariable String diagnosticHeaderId) {
		log.info(">> deleteDiagnosticServicePackageHeader({})", diagnosticHeaderId);
		headerService.deleteDiagnosticServicePackageHeader(diagnosticHeaderId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
