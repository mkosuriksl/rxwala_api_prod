package com.kosuri.stores.controller;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kosuri.stores.dao.GenerateInvoiceEntity;
import com.kosuri.stores.handler.GenerateInvoiceService;
import com.kosuri.stores.model.dto.GenerateInvoiceRequestDTO;
import com.kosuri.stores.model.dto.ResponseGetGenerateInvoiceNumberDto;

import jakarta.validation.Valid;

@RestController
public class GenerateInvoiceNumberController {
	
	@Autowired
	private GenerateInvoiceService generateInvoiceService;

	@PostMapping("/generateInvoiceNumber")
	public ResponseEntity<GenerateInvoiceEntity> generateInvoice(@Valid @RequestBody GenerateInvoiceRequestDTO invoice) {
	    GenerateInvoiceEntity savedInvoice = generateInvoiceService.generateAndSaveInvoice(invoice);
	    return ResponseEntity.ok(savedInvoice);
	}

	@PutMapping("/update-invoice")
	public ResponseEntity<GenerateInvoiceEntity> updateInvoiceFields(@RequestBody GenerateInvoiceRequestDTO dto) {
	    GenerateInvoiceEntity updatedInvoice = generateInvoiceService.updateInvoiceFields(dto);
	    return ResponseEntity.ok(updatedInvoice);
	}

	
	@GetMapping("/get-invoice")
	public ResponseEntity<ResponseGetGenerateInvoiceNumberDto> getTask(
			@RequestParam(required = false) String invNumber, @RequestParam(required = false) String ponumber,
			@RequestParam(required = false) Double amount, @RequestParam(required = false) String status,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<GenerateInvoiceEntity> srpqPage = generateInvoiceService.getInvoiceNumber(invNumber, ponumber, amount, status,
				pageable);
		ResponseGetGenerateInvoiceNumberDto response = new ResponseGetGenerateInvoiceNumberDto();

		response.setMessage("Generate Invoice retrieved successfully.");
		response.setStatus(true);
		response.setGenerateInvoiceEntity(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}


}
