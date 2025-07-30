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

import com.kosuri.stores.dao.DiagnosticServicePackageLine;
import com.kosuri.stores.handler.DiagnosticServicePackageLineService;
import com.kosuri.stores.model.dto.DiagnosticServicePackageLineRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/diagnostic-service-package-line")
public class DiagnosticServicePackageLineController {

	@Autowired
	private DiagnosticServicePackageLineService headerService;

	@GetMapping
	public ResponseEntity<List<DiagnosticServicePackageLine>> getDiagnosticServicePackageLines(
			@RequestParam(required = false) String storeId, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String packageId) {
		log.info(">> getDiagnosticServicePackageLines()");
		return ResponseEntity.ok(headerService.getDiagnosticServicePackageLines(storeId, userId, packageId));
	}

	@GetMapping("/{diagnosticLineId}")
	public ResponseEntity<DiagnosticServicePackageLine> getDiagnosticServicePackageLineById(
			@PathVariable String diagnosticLineId) {
		log.info(">> getDiagnosticServicePackageLineById({})", diagnosticLineId);
		return ResponseEntity.ok(headerService.getDiagnosticServicePackageLineById(diagnosticLineId));
	}

	@PostMapping("/add")
	public ResponseEntity<Void> AddDiagnosticServicePackageLine(@RequestBody DiagnosticServicePackageLineRequest req) {
		log.info(">> createDiagnosticServicePackageLine({})", req);
		headerService.addDiagnosticServicePackageLines(req);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{diagnosticLineId}")
	public ResponseEntity<Void> updateDiagnosticServicePackageLine(@PathVariable String diagnosticLineId,
			@RequestBody DiagnosticServicePackageLineRequest req) {
		log.info(">> updateDiagnosticServicePackageLine({}, {})", diagnosticLineId, req);
		headerService.updateDiagnosticServicePackageLine(diagnosticLineId, req);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/{diagnosticLineId}")
	public ResponseEntity<Void> deleteDiagnosticServicePackageLine(@PathVariable String diagnosticLineId) {
		log.info(">> deleteDiagnosticServicePackageLine({})", diagnosticLineId);
		headerService.deleteDiagnosticServicePackageLine(diagnosticLineId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
