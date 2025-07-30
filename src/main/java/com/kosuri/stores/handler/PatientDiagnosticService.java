package com.kosuri.stores.handler;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.PatientDiagnostic;
import com.kosuri.stores.dao.PatientDiagnosticRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.model.dto.GenericResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class PatientDiagnosticService {

	@Autowired
	private PatientDiagnosticRepository repository;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public GenericResponse<List<PatientDiagnostic>> savePatient(List<PatientDiagnostic> patients) {
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);

	    if (login.isEmpty()) {
	        return new GenericResponse<>("error", "Access denied. This API is restricted to store users only.", null);
	    }
	    try {
	        for (PatientDiagnostic patient : patients) {
	            patient.setUpdatedBy(login.get().getUserId());
	            patient.setUpdatedDate(new Date());
	        }
	        List<PatientDiagnostic> savedItems = repository.saveAll(patients);

	        return new GenericResponse<>("success", "Patient saved successfully", savedItems);

	    } catch (Exception e) {
	        return new GenericResponse<>("error", "Failed to save patient: " + e.getMessage(), null);
	    }
	}	
}
