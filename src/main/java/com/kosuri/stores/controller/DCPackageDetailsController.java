package com.kosuri.stores.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.DCPackageDetails;
import com.kosuri.stores.dao.DCPackageDetailsHistory;
import com.kosuri.stores.handler.DCPackageService;
import com.kosuri.stores.model.dto.PackageRequestDto;
import com.kosuri.stores.model.dto.PackageRequestDto1;
import com.kosuri.stores.model.dto.StoreWithDiagnosticServiceResponseDto;
import com.kosuri.stores.model.dto.StoreWithPrimaryCareResponseDto;

@RestController
@RequestMapping("/dc-packages")
public class DCPackageDetailsController {

    @Autowired
    private DCPackageService packageService;

    @PostMapping("/addPackage")
    public PackageRequestDto addPackageDetails(@RequestBody PackageRequestDto packageRequestList) {
        return packageService.createPackageDetails(packageRequestList);
    }
    
    @PutMapping("/updatePackage")
    public ResponseEntity<PackageRequestDto> updatePackageDetails(@RequestBody PackageRequestDto packageRequestDto) {
        PackageRequestDto updatedPackage = packageService.updatePackageDetails(packageRequestDto);
        return ResponseEntity.ok(updatedPackage);
    }
    
    @GetMapping("/get-details")
    public ResponseEntity<List<DCPackageDetails>> getDCPackageDetails(
            @RequestParam(required = false) String packageIdLineId,@RequestParam(required = false) String packageId,
            @RequestParam(required = false) String amount,@RequestParam(required = false) String discount,
            @RequestParam(required = false) String serviceId,@RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String updatedBy,@RequestParam Map<String, String> requestParams) {

            List<DCPackageDetails> result = packageService.getDCPackageDetails(packageIdLineId,packageId,amount,discount,serviceId,serviceName,
        			updatedBy,requestParams);
            return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get-detailsHistory")
    public ResponseEntity<List<DCPackageDetailsHistory>> getDCPackageDetailsHistory(
    		@RequestParam(required = false) String packageIdLineId,@RequestParam(required = false) String packageId,
            @RequestParam(required = false) String amount,@RequestParam(required = false) String discount,
            @RequestParam(required = false) String serviceId,@RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String updatedBy,@RequestParam Map<String, String> requestParams) {

            List<DCPackageDetailsHistory> result = packageService.getDCPackageDetailsHistory(packageIdLineId,packageId,amount,discount,serviceId,serviceName,
        			updatedBy,requestParams);
            return ResponseEntity.ok(result);
    }
    
    @GetMapping("/home-search-diagnostic")
    public ResponseEntity<StoreWithDiagnosticServiceResponseDto> getStoresWithServices(
            @RequestParam String location,
            @RequestParam String storeCategory,  // Make storeCategory mandatory
            @RequestParam(required = false) String serviceCategory) {

        StoreWithDiagnosticServiceResponseDto response = packageService.getLocationStoreWithServices(location, storeCategory, serviceCategory);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/home-search-primarycare")
    public ResponseEntity<StoreWithPrimaryCareResponseDto> getStoresWithPrimaryCare(
            @RequestParam String location,
            @RequestParam String storeCategory,  // Make storeCategory mandatory
            @RequestParam(required = false) String serviceCategory) {

    	StoreWithPrimaryCareResponseDto response = packageService.getLocationStoreWithPrimary(location, storeCategory, serviceCategory);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getPackageDetails")
    public List<PackageRequestDto> getPackageDetails(@RequestParam(required = false) String packageId, 
                                                     @RequestParam(required = false) String packageName,
                                                     @RequestParam(required = false) String storeId,
                                                     @RequestParam(required = false) String userId) {
        return packageService.getPackageDetails(Optional.ofNullable(packageId), 
                                                Optional.ofNullable(packageName), 
                                                Optional.ofNullable(storeId), 
                                                Optional.ofNullable(userId));
    }

    @GetMapping("/getHomeSearch-PackageDetails")
    public List<PackageRequestDto1> getHomeSearchPackageDetails(@RequestParam(required = false) String userIdStoreId) {
        return packageService.getHomeSearchPackageDetails(Optional.ofNullable(userIdStoreId));
    }

    
}