package com.kosuri.stores.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosuri.stores.dao.ResponseGetSaleEntityPaginatedResponse;
import com.kosuri.stores.dao.ResponseGetSaleInvoicePaginatedResponse;
import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.SaleUpdateRequestDto;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.handler.SaleHandler;
import com.kosuri.stores.model.dto.ApiResponse;
import com.kosuri.stores.model.dto.GstSummaryResponseDto;
import com.kosuri.stores.model.dto.SaleInvoiceRequest;
import com.kosuri.stores.model.dto.SaleInvoiceResponseDto;
import com.kosuri.stores.model.dto.SaleUpdateFinalResponse;
import com.kosuri.stores.model.response.GenericResponse;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sale")
public class SaleController {

	@Autowired
	private SaleHandler saleHandler;

	@PostMapping("/import")
	public ResponseEntity<GenericResponse> mapReapExcelDatatoDB(@RequestParam("file") MultipartFile reapExcelDataFile,
			@Nonnull @RequestParam("store_id") String storeId, @RequestParam("email_id") String emailId) {
		GenericResponse response = new GenericResponse();
		try {
			saleHandler.createSaleEntityFromRequest(reapExcelDataFile, storeId, emailId);
			response.setResponseMessage("Successfully uploaded the file!");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (IOException e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} catch (Exception e) {
			response.setResponseMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

//	@PostMapping("/invoice")
//	public ResponseEntity<?> saveSalesInvoices(@RequestBody List<SaleInvoiceRequest> saleInvoiceRequests) {
//		return ResponseEntity.ok(saleHandler.saveSalesInvoices(saleInvoiceRequests));
//	}
	
	@PostMapping("/add")
	public ResponseEntity<?> saveSalesInvoicesEn(@RequestBody SaleInvoiceRequest saleInvoiceRequests) {
//		return ResponseEntity.ok(saleHandler.saveSalesInvoicesEn(saleInvoiceRequests));
		List<SaleEntity> savedPurchases = saleHandler.saveSalesInvoicesEn(saleInvoiceRequests);

		ApiResponse<List<SaleEntity>> response = new ApiResponse<>("Sale data is added successfully", true,
				savedPurchases);
		return ResponseEntity.ok(response);
	}
	
//	@GetMapping("/report/get-sale-invoice")
//	public ResponseEntity<List<SaleInvoiceResponseDto>> getPurchaseDetails(
//	        @RequestParam(required = false) String docNumber,
//	        @RequestParam(required = false) String custName,
//	        @RequestParam(required = false) String storeId,
//	        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
//	        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
//	        @RequestParam(required = false) String userIdstoreId,
//	        HttpServletRequest request) {
//
//	    validateUnexpectedParams(request, Set.of("docNumber", "custName", "storeId", "fromDate", "toDate","userIdstoreId"));
//
//	    List<SaleInvoiceResponseDto> purchases = saleHandler.getSale(docNumber, custName, storeId, fromDate, toDate,userIdstoreId);
//	    return ResponseEntity.ok(purchases);
//	}
	
	@GetMapping("/report/get-sale-invoice")
	public ResponseEntity<ResponseGetSaleInvoicePaginatedResponse> getSaleInvoice(
	        @RequestParam(required = false) String docNumber,
	        @RequestParam(required = false) String custName,
	        @RequestParam(required = false) String storeId,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
	        @RequestParam(required = false) String userIdstoreId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        HttpServletRequest request) {

	    validateUnexpectedParams(request, Set.of("docNumber", "custName", "storeId", "fromDate", "toDate", "userIdstoreId", "page", "size"));

	    Pageable pageable = PageRequest.of(page, size);
	    Page<SaleInvoiceResponseDto> salePage = saleHandler.getSale(docNumber, custName, storeId, fromDate, toDate, userIdstoreId, pageable);

	    ResponseGetSaleInvoicePaginatedResponse response = new ResponseGetSaleInvoicePaginatedResponse();
	    response.setMessage("Sale invoices retrieved successfully.");
	    response.setStatus(true);
	    response.setInvoices(salePage.getContent());
	    response.setCurrentPage(salePage.getNumber());
	    response.setPageSize(salePage.getSize());
	    response.setTotalElements(salePage.getTotalElements());
	    response.setTotalPages(salePage.getTotalPages());

	    return ResponseEntity.ok(response);
	}

	
	@GetMapping("/report/get-customer-sale-invoice-by-search")
	public ResponseEntity<Map<String, Object>> getCustNameBySearch(@RequestParam(required = false) String custName) {
		return ResponseEntity.ok(saleHandler.getCustNameBySearch(custName));
	}
	private void validateUnexpectedParams(HttpServletRequest request, Set<String> allowedParams) {
	    Map<String, String[]> paramMap = request.getParameterMap();
	    for (String param : paramMap.keySet()) {
	        if (!allowedParams.contains(param)) {
	            throw new ResourceNotFoundException("Unexpected parameter: " + param);
	        }
	    }
	}
	
//	@GetMapping("/report/get-sale-invoice-details")
//	public ResponseEntity<List<SaleEntity>> getPurchasesInovices(
//	        @RequestParam(required = false) String docNumber,
//	        @RequestParam(required = false) String userIdstoreId,
//	        HttpServletRequest request) {
//
//	    validateUnexpectedParams(request, Set.of("docNumber","userIdstoreId"));
//
//	    List<SaleEntity> purchases = saleHandler.getSaleInovices(docNumber,userIdstoreId);
//	    return ResponseEntity.ok(purchases);
//	}
	
	@GetMapping("/report/get-sale-invoice-details")
	public ResponseEntity<ResponseGetSaleEntityPaginatedResponse> getPurchasesInvoices(
	        @RequestParam(required = false) String docNumber,
	        @RequestParam(required = false) String userIdstoreId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        HttpServletRequest request) {

	    validateUnexpectedParams(request, Set.of("docNumber", "userIdstoreId", "page", "size"));
	    
	    Pageable pageable = PageRequest.of(page, size);
	    Page<SaleEntity> salePage = saleHandler.getSaleInvoices(docNumber, userIdstoreId, pageable);

	    ResponseGetSaleEntityPaginatedResponse response = new ResponseGetSaleEntityPaginatedResponse();
	    response.setMessage("Sale invoices retrieved successfully.");
	    response.setStatus(true);
	    response.setSales(salePage.getContent());
	    response.setCurrentPage(salePage.getNumber());
	    response.setPageSize(salePage.getSize());
	    response.setTotalElements(salePage.getTotalElements());
	    response.setTotalPages(salePage.getTotalPages());

	    return ResponseEntity.ok(response);
	}


	
	@PutMapping("/sale-update-details")
    public ResponseEntity<SaleUpdateFinalResponse> updateSaleRecordsByInvoice(
            @RequestBody SaleUpdateRequestDto requestDto) {
        
        SaleUpdateFinalResponse response = saleHandler.updatePurchasesByInvoice(requestDto);
        return ResponseEntity.ok(response);
    }
	
	@PutMapping("/update-sale-invoice")
    public ResponseEntity<?> updateSaleList(@RequestBody List<SaleEntity> updatedEntities) {
        Map<String, Object> response = saleHandler.updateSale(updatedEntities);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/gst-details")
	 public ResponseEntity<List<GstSummaryResponseDto>> getGstSummary(
	         @RequestParam(required = false) String storeId,
	         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
	         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
	 ) {
	     List<GstSummaryResponseDto> summary = saleHandler.getGstSummary(storeId, fromDate, toDate);
	     return ResponseEntity.ok(summary);
	 }
}