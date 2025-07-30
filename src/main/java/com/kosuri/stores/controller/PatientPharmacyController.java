package com.kosuri.stores.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.PatientPharmacy;
import com.kosuri.stores.handler.PatientPharmacyService;
import com.kosuri.stores.model.dto.GenericResponse;

@RestController
@RequestMapping("/patient/pharmacy")
public class PatientPharmacyController {

	@Autowired
	private PatientPharmacyService patientPharmacyService;

	@PostMapping("/add")
	public ResponseEntity<GenericResponse<List<PatientPharmacy>>> addItems(@RequestBody List<PatientPharmacy> items) {
		GenericResponse<List<PatientPharmacy>> response = patientPharmacyService.savePatient(items);
		return ResponseEntity.ok(response);
	}

}
