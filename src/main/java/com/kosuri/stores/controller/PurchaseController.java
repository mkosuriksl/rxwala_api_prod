package com.kosuri.stores.controller;

import java.io.IOException;
import java.util.Date;
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

import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.PurchaseUpdateRequestDto;
import com.kosuri.stores.dao.ResponseGetPurchaseInvoiceDetailsPaginatedResponse;
import com.kosuri.stores.dao.ResponseGetSaleEntityPaginatedResponse;
import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.handler.PurchaseHandler;
import com.kosuri.stores.handler.RepositoryHandler;
import com.kosuri.stores.model.dto.ApiResponse;
import com.kosuri.stores.model.dto.GstSummaryResponseDto;
import com.kosuri.stores.model.dto.PurchaseInvoiceRequest;
import com.kosuri.stores.model.dto.PurchaseInvoiceResponseDto;
import com.kosuri.stores.model.dto.PurchaseUpdateFinalResponse;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.ResponseGetPurchaseInvoiceDto;
import com.kosuri.stores.model.response.SearchResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/purchase")
public class PurchaseController {
	@Autowired
	private RepositoryHandler repositoryHandler;
	@Autowired
	private PurchaseHandler purchaseHandler;

	@PostMapping("/import")
	public ResponseEntity<GenericResponse> mapReapExcelDatatoDB(@RequestParam("file") MultipartFile reapExcelDataFile,
			@RequestParam("store_id") String storeId, @RequestParam("email_id") String emailId) {
		GenericResponse response = new GenericResponse();
		try {
			purchaseHandler.createPurchaseEntityFromRequest(reapExcelDataFile, storeId, emailId);
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

	@GetMapping("/searchByBusinessType")
	public ResponseEntity<SearchResponse> getStock(@RequestParam("businessType") String businessType,
			@RequestParam("storeId") String storeId) throws APIException {
		SearchResponse searchResponse = new SearchResponse();
		try {
			List<StockEntity> stockEntityList = purchaseHandler.searchStockByBusinessType(businessType, storeId);
			searchResponse.setStockList(stockEntityList);
		} catch (APIException e) {
			searchResponse.setResponseMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(searchResponse);
		} catch (Exception e) {
			searchResponse.setResponseMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(searchResponse);
		}
		return null;
	}
	@PostMapping("/invoice")
	public ResponseEntity<?> savePurchaseInvoices(@RequestBody PurchaseInvoiceRequest invoiceRequest) {
		log.info(">> savePurchaseInvoices({})", invoiceRequest);
		return ResponseEntity.ok(purchaseHandler.savePurchaseInvoices(invoiceRequest));
	}
	
	@PostMapping("/add")
	public ResponseEntity<?> savePurchaseInvoicesEn(@RequestBody PurchaseInvoiceRequest invoiceRequest) {
		log.info(">> savePurchaseInvoices({})", invoiceRequest);
		List<PurchaseEntity> savedPurchases = purchaseHandler.savePurchaseInvoicesEn(invoiceRequest);
	    
		ApiResponse<List<PurchaseEntity>> response = new ApiResponse<>(
	        "Purchase data is added successfully",
	        true,
	        savedPurchases
	    );
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/report/get-purchase-invoice")
	public ResponseEntity<ResponseGetPurchaseInvoiceDto> getPurchaseDetails(
	        @RequestParam(required = false) String invoiceNo,
	        @RequestParam(required = false) String suppName,
	        @RequestParam(required = false) String storeId,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date toDate,
	        @RequestParam(required = false) String userIdstoreId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        HttpServletRequest request) {

	    validateUnexpectedParams(request, Set.of("invoiceNo", "suppName", "storeId", "fromDate", "toDate","userIdstoreId","page", "size"));
	    Pageable pageable = PageRequest.of(page, size);
	    Page<PurchaseInvoiceResponseDto> purchases = purchaseHandler.getFilteredPurchases(invoiceNo, suppName, storeId, fromDate, toDate,userIdstoreId,pageable);

	    ResponseGetPurchaseInvoiceDto response = new ResponseGetPurchaseInvoiceDto();
	    response.setMessage("Sale invoices retrieved successfully.");
	    response.setStatus(true);
	    response.setPurchase(purchases.getContent());
	    response.setCurrentPage(purchases.getNumber());
	    response.setPageSize(purchases.getSize());
	    response.setTotalElements(purchases.getTotalElements());
	    response.setTotalPages(purchases.getTotalPages());

	    return ResponseEntity.ok(response);
	}
	
//	@GetMapping("/report/get-purchase-invoice-details")
//	public ResponseEntity<List<PurchaseEntity>> getPurchasesInovices(
//	        @RequestParam(required = false) String invoiceNo,
//	        @RequestParam(required = false) String userIdstoreId,
//	        HttpServletRequest request) {
//
//	    validateUnexpectedParams(request, Set.of("invoiceNo","userIdstoreId"));
//
//	    List<PurchaseEntity> purchases = purchaseHandler.getPurchasesInovices(invoiceNo,userIdstoreId);
//	    return ResponseEntity.ok(purchases);
//	}

	@GetMapping("/report/get-purchase-invoice-details")
	public ResponseEntity<ResponseGetPurchaseInvoiceDetailsPaginatedResponse> getPurchasesInvoices(
	        @RequestParam(required = false) String invoiceNo,
	        @RequestParam(required = false) String userIdstoreId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        HttpServletRequest request) {

	    validateUnexpectedParams(request, Set.of("invoiceNo", "userIdstoreId", "page", "size"));
	    
	    Pageable pageable = PageRequest.of(page, size);
	    Page<PurchaseEntity> salePage = purchaseHandler.getPurchasesInovices(invoiceNo, userIdstoreId, pageable);

	    ResponseGetPurchaseInvoiceDetailsPaginatedResponse response = new ResponseGetPurchaseInvoiceDetailsPaginatedResponse();
	    response.setMessage("Purchase invoices retrieved successfully.");
	    response.setStatus(true);
	    response.setPurchases(salePage.getContent());
	    response.setCurrentPage(salePage.getNumber());
	    response.setPageSize(salePage.getSize());
	    response.setTotalElements(salePage.getTotalElements());
	    response.setTotalPages(salePage.getTotalPages());

	    return ResponseEntity.ok(response);
	}
	
	private void validateUnexpectedParams(HttpServletRequest request, Set<String> allowedParams) {
	    Map<String, String[]> paramMap = request.getParameterMap();
	    for (String param : paramMap.keySet()) {
	        if (!allowedParams.contains(param)) {
	            throw new ResourceNotFoundException("Unexpected parameter: " + param);
	        }
	    }
	}

	@PutMapping("/purchase-update-details")
    public ResponseEntity<PurchaseUpdateFinalResponse> updatePurchaseRecordsByInvoice(
            @RequestBody PurchaseUpdateRequestDto requestDto) {
        
        PurchaseUpdateFinalResponse response = purchaseHandler.updatePurchasesByInvoice(requestDto);
        return ResponseEntity.ok(response);
    }
	
	 @PutMapping("/update-purchase-invoice")
	    public ResponseEntity<?> updatePurchaseList(@RequestBody List<PurchaseEntity> updatedEntities) {
	        Map<String, Object> response = purchaseHandler.updatePurchase(updatedEntities);
	        return ResponseEntity.ok(response);
	    }
	 
	 @GetMapping("/gst-details")
	 public ResponseEntity<List<GstSummaryResponseDto>> getGstSummary(
	         @RequestParam(required = false) String storeId,
	         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
	         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
	 ) {
	     List<GstSummaryResponseDto> summary = purchaseHandler.getGstSummary(storeId, fromDate, toDate);
	     return ResponseEntity.ok(summary);
	 }

	 @GetMapping("/gst-sale-purchase-summary")
	 public Map<String, Object> getCombinedGstSummary(
	         @RequestParam(required = false) String storeId,
	         @RequestParam(required = false) String userId,
	         @RequestParam(required = false) String userIdStoreId,
	         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
	         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

	     return purchaseHandler.getCombinedGstSummary(storeId, userId, userIdStoreId, fromDate, toDate);
	 }

}
