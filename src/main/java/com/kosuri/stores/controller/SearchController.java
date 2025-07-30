package com.kosuri.stores.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.handler.SearchHandler;
import com.kosuri.stores.model.dto.StockDistributorResult;
import com.kosuri.stores.model.response.SearchResponse;
import com.kosuri.stores.model.search.SearchResult;
import com.kosuri.stores.model.search.StockAndDiscountEnResult;
import com.kosuri.stores.model.search.StockAndDiscountResult;


@RestController
public class SearchController {

    @Autowired
    SearchHandler searchHandler;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<SearchResponse> search(@RequestParam("medicine") String medicine, @RequestParam("location") String location,
                                                 @RequestParam(value = "category") String category) {
        List<SearchResult> searchResultList = searchHandler.search(medicine, location, category);
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setSearchResultList(searchResultList);
        return ResponseEntity.status(HttpStatus.OK).body(searchResponse);
    }
  
    @GetMapping("/home-search-medicine")
    public ResponseEntity<Map<String, Object>> getStockDetails(@RequestParam  String medicineName, String location,@RequestParam(required=false) String mfName) {
        List<StockDistributorResult> results = searchHandler.getMedicineDetails(medicineName, location,mfName);

        Map<String, Object> response = new HashMap<>();

        if (results.isEmpty()) {
            response.put("status", false);
            response.put("message", "No stock details found for the given medicine and location.");
        } else {
            response.put("status", true);
            response.put("message", "Stock details fetched successfully.");
            response.put("data", results); // Include the list in the response
        }

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/search-medicine-by-distributor")
//    public ResponseEntity<Map<String, Object>> getStockDetailsEn(@RequestParam(required = false)  String medicineName, @RequestParam String location,@RequestParam(required=false) String mfName,
//    		@RequestParam(required=false) String userId) {
//        List<StockAndDiscountEnResult> results = searchHandler.getMedicineDetailsEn(medicineName, location,mfName,userId);
//
//        Map<String, Object> response = new HashMap<>();
//
//        if (results.isEmpty()) {
//            response.put("status", false);
//            response.put("message", "No Distributor details found for the given medicine and location.");
//        } else {
//            response.put("status", true);
//            response.put("message", "Distributor details fetched successfully.");
//            response.put("data", results); // Include the list in the response
//        }
//
//        return ResponseEntity.ok(response);
//    }
    
//    @GetMapping("/search-medicine-by-distributor")
//    public ResponseEntity<Map<String, Object>> getStockDetailsEn(
//            @RequestParam(required = false) String medicineName,
//            @RequestParam String location,
//            @RequestParam(required = false) String mfName,
//            @RequestParam(required = false) String userId,
//            @RequestParam(required = false) String storeBusinessType,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//    	Page<StockDistributorResult>results = searchHandler.getMedicineDetailsEn(medicineName, location, mfName, userId,storeBusinessType,page, size);
//
//        Map<String, Object> response = new HashMap<>(); 
//        if (results.isEmpty()) {
//            response.put("status", false);
//            response.put("message", "No Distributor details found for the given medicine and location.");
//            response.put("data", results.getContent());
//            response.put("currentPage", results.getNumber());
//            response.put("pageSize", results.getSize());
//            response.put("totalPages", results.getTotalPages());
//            response.put("totalElements", results.getTotalElements());
//        } else {
//            response.put("status", true);
//            response.put("message", "Distributor details fetched successfully.");
//            response.put("data", results);
//        }
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/search-medicine-by-distributor")
    public ResponseEntity<Map<String, Object>> getStockDetailsEn(
            @RequestParam(required = false) String medicineName,
            @RequestParam String location,
            @RequestParam(required = false) String mfName,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String storeBusinessType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<StockDistributorResult> results = searchHandler.getMedicineDetailsEn(
            medicineName, location, mfName, userId, storeBusinessType, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("message", results.isEmpty() ? "No Distributor details found." : "Distributor details fetched successfully.");
        response.put("data", results.getContent());
        response.put("currentPage", results.getNumber());
        response.put("pageSize", results.getSize());
        response.put("totalPages", results.getTotalPages());
        response.put("totalElements", results.getTotalElements());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/storeName")
    public ResponseEntity<List<Map<String, String>>> getStoreByName(@RequestParam(required = false) String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Map<String, String>> response = searchHandler.getUserIdAndStoreIdByName(name);
        return ResponseEntity.ok(response);
    }


}


