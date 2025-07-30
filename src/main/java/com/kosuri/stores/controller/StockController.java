package com.kosuri.stores.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.handler.StockHandler;
import com.kosuri.stores.model.dto.StockMrpUpdateRequest;
import com.kosuri.stores.model.dto.StockRequest;
import com.kosuri.stores.model.response.GenericResponse;

import jakarta.annotation.Nonnull;

@RestController
@RequestMapping("/stock")
public class StockController {

	@Autowired
	private StockHandler stockHandler;

	@PostMapping("/import")
	public ResponseEntity<GenericResponse> importDataInStock(@RequestParam("file") MultipartFile reapExcelDataFile,
			@Nonnull @RequestParam("store_id") String storeId, @RequestParam("email_id") String emailId) {
		GenericResponse response = new GenericResponse();
		try {
			stockHandler.importDataInStock(reapExcelDataFile, storeId, emailId);
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

	@PostMapping("/invoice")
	public ResponseEntity<?> saveStocksInvoices(@RequestBody List<StockRequest> stockRequests) {
		return ResponseEntity.ok(stockHandler.saveStocksInvoices(stockRequests));
	}

	@PostMapping("/add")
	public ResponseEntity<?> saveStocksInvoicesEn(@RequestBody List<StockRequest> stockRequests) {
		return ResponseEntity.ok(stockHandler.saveStocksInvoicesEn(stockRequests));
	}

	@GetMapping("/get-stock-report")
	public ResponseEntity<Map<String, Object>> getStockReport(@RequestParam(required = false) String itemName,
			@RequestParam(required = false) String supplierName, @RequestParam(required = false) String itemCategory,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromExpiryDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date toExpiryDate,
			@RequestParam(required = false) String storeId, @RequestParam String userIdStoreId,
			@RequestParam String userId) {
		return ResponseEntity.ok(stockHandler.getStockReport(itemName, supplierName, itemCategory, fromExpiryDate,
				toExpiryDate, storeId, userIdStoreId, userId));
	}

	@GetMapping("/get-stock-by-itemCode")
	public ResponseEntity<Map<String, Object>> getstockbyItemCode(@RequestParam(required = false) String itemCode,
			@RequestParam(required = false) String userIdStoreId, @RequestParam(required = false) String userId) {
		return ResponseEntity.ok(stockHandler.getstockbyItemCode(itemCode, userIdStoreId, userId));
	}

	@GetMapping("/get-itemCode-by-itemName")
	public ResponseEntity<Map<String, Object>> getStockByItemName(@RequestParam(required = false) String itemName) {
		return ResponseEntity.ok(stockHandler.getStockByItemName(itemName));
	}

	@GetMapping("/get-itemName-by-search")
	public ResponseEntity<Map<String, Object>> getStockByItemNames(@RequestParam(required = false) String itemName) {
		return ResponseEntity.ok(stockHandler.getStockByItemNames(itemName));
	}

	@GetMapping("/get-itemCode-by-itemName-enhanced")
	public ResponseEntity<Map<String, Object>> getStockByItemNameEnhanced(
			@RequestParam(required = false) String itemName) {
		return ResponseEntity.ok(stockHandler.getStockByItemNameEnhanced(itemName));
	}

	@GetMapping("/get-distinct-itemName")
	public List<String> getItemName() {
		return stockHandler.getItemName();
	}

	@GetMapping("/search-medicine-by-supplier")
	public ResponseEntity<List<StockEntity>> getStoresByLocationAndBusinessType(@RequestParam String storeId) {
		List<StockEntity> stores = stockHandler.getStocksByStoreId(storeId);
		if (stores.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(stores);
	}

	@PutMapping("/update-stock")
	public ResponseEntity<String> updateMrp(@RequestBody StockMrpUpdateRequest request) {
		String result = stockHandler.updateMrpByUserIdStoreIdItemCode(request);
		return ResponseEntity.ok(result);
	}
}