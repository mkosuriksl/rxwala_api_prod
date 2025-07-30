package com.kosuri.stores.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.DCPackageHeader;
import com.kosuri.stores.dao.DCPackageHeaderHistory;
import com.kosuri.stores.handler.DCPackageHeaderService;

@RestController
@RequestMapping("/dc-packages")
public class DCPackageHeaderController {

    @Autowired
    private DCPackageHeaderService dcPackageHeaderService;

    @GetMapping("/get-header")
    public ResponseEntity<List<DCPackageHeader>> getDCPackageHeader(
            @RequestParam(required = false) String packageId,
            @RequestParam(required = false) String totalAmount,
            @RequestParam(required = false) String userIdStoreId,
            @RequestParam(required = false) String packageName,
            @RequestParam(required = false) String updatedBy,
            @RequestParam Map<String, String> requestParams) {

            List<DCPackageHeader> result = dcPackageHeaderService.getDCPackageHeader(packageId,userIdStoreId,packageName, totalAmount, updatedBy, requestParams);
            return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get-headerHistory")
    public ResponseEntity<List<DCPackageHeaderHistory>> getDCPackageHeaderHistory(
    		@RequestParam(required = false) String packageId,
            @RequestParam(required = false) String totalAmount,
            @RequestParam(required = false) String userIdStoreId,
            @RequestParam(required = false) String packageName,
            @RequestParam(required = false) String updatedBy,
            @RequestParam Map<String, String> requestParams) {

            List<DCPackageHeaderHistory> result = dcPackageHeaderService.getDCPackageHeaderHistory(packageId,userIdStoreId,packageName, totalAmount, updatedBy, requestParams);
            return ResponseEntity.ok(result);
    }
    
}