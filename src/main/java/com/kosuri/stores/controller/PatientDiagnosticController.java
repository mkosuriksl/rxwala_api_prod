package com.kosuri.stores.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.PatientDiagnostic;
import com.kosuri.stores.handler.PatientDiagnosticService;
import com.kosuri.stores.model.dto.GenericResponse;

@RestController
@RequestMapping("/patient/diagnostic")
public class PatientDiagnosticController {

	@Autowired
	private PatientDiagnosticService patientDiagnosticService ;

	@PostMapping("/add")
	public ResponseEntity<GenericResponse<List<PatientDiagnostic>>> addItems(@RequestBody List<PatientDiagnostic> items) {
		GenericResponse<List<PatientDiagnostic>> response = patientDiagnosticService.savePatient(items);
		return ResponseEntity.ok(response);
	}

}
