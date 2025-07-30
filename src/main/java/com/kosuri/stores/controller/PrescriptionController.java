package com.kosuri.stores.controller;

import com.kosuri.stores.dao.Prescription;
import com.kosuri.stores.dao.PrescriptionHistory;
import com.kosuri.stores.dao.ResponseGetPrescriptionDto;
import com.kosuri.stores.dao.ResponseGetPrescriptionHistoryAndPrescriptionDto;
import com.kosuri.stores.dao.ResponseGetPrescriptionHistoryDto;
import com.kosuri.stores.dao.VisitPrescriptionGroupDto;
import com.kosuri.stores.handler.PrescriptionHandler;
import com.kosuri.stores.model.dto.GenericResponse;
import com.kosuri.stores.model.dto.PrescriptionRequest;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionHandler prescriptionService;

//    @PostMapping("/add")
//    public ResponseEntity<GenericResponse<Prescription>> createPrescription(@RequestBody Prescription prescription) {
//        GenericResponse<Prescription> response = prescriptionService.savePrescription(prescription);
//        if ("false".equals(response.getStatus())) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409
//        }
//        return ResponseEntity.ok(response);
//    }
    
    @PostMapping("/add")
    public ResponseEntity<GenericResponse<List<Prescription>>> createPrescription(@RequestBody PrescriptionRequest request) {
        GenericResponse<List<Prescription>> response = prescriptionService.savePrescription(request);
        if ("false".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.ok(response);
    }


//    @PutMapping("/update")
//    public ResponseEntity<GenericResponse<Prescription>> updatePrescription(@RequestBody Prescription prescription) {
//        return ResponseEntity.ok(prescriptionService.updatePrescription(prescription));
//    }
    
    @PutMapping("/update")
    public ResponseEntity<GenericResponse<List<Prescription>>> updatePrescriptions(
            @RequestBody List<Prescription> updates) {
        return ResponseEntity.ok(prescriptionService.updatePrescriptions(updates));
    }

    
    @GetMapping("/get-prescrption-history")
	public ResponseEntity<ResponseGetPrescriptionHistoryDto> getTask(
			@RequestParam(required = false) String visitOrdNoLineId, @RequestParam(required = false) String visitOrdNo,
			@RequestParam(required = false) String medicineName, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String userIdStoreId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<PrescriptionHistory> srpqPage = prescriptionService.getPrescriptionHistory(visitOrdNoLineId, visitOrdNo, medicineName, userId,
				userIdStoreId, pageable);
		ResponseGetPrescriptionHistoryDto response = new ResponseGetPrescriptionHistoryDto();

		response.setMessage("Prescrption history details retrieved successfully.");
		response.setStatus(true);
		response.setPrescriptionHistory(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
    
    @GetMapping("/get-prescrption")
	public ResponseEntity<ResponseGetPrescriptionDto> getPrescption(
			@RequestParam(required = false) String visitOrdNo,
			@RequestParam(required = false) String medicineName, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String userIdStoreId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<Prescription> srpqPage = prescriptionService.getPrescription(visitOrdNo, medicineName, userId,
				userIdStoreId, pageable);
		ResponseGetPrescriptionDto response = new ResponseGetPrescriptionDto();

		response.setMessage("Prescrption details retrieved successfully.");
		response.setStatus(true);
		response.setPrescription(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
    
    @GetMapping("/get-prescription-and-history")
    public ResponseEntity<ResponseGetPrescriptionHistoryAndPrescriptionDto> getGroupedHistory(
            @RequestParam(required = false) String visitOrdNo,
            @RequestParam(required = false) String medicineName,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String userIdStoreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VisitPrescriptionGroupDto> groupedPage = prescriptionService.getGroupedPrescriptionHistory(
                visitOrdNo, medicineName, userId, userIdStoreId, pageable
        );

        ResponseGetPrescriptionHistoryAndPrescriptionDto response = new ResponseGetPrescriptionHistoryAndPrescriptionDto();
        response.setMessage("Prescription history details retrieved successfully.");
        response.setStatus(true);
        response.setPrescriptionGroups(groupedPage.getContent());
        response.setCurrentPage(groupedPage.getNumber());
        response.setPageSize(groupedPage.getSize());
        response.setTotalElements(groupedPage.getTotalElements());
        response.setTotalPages(groupedPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

}

