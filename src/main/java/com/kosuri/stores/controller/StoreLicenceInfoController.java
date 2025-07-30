package com.kosuri.stores.controller;

import com.kosuri.stores.dao.StoreLicenceInfoEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.DiagnosticHandler;
import com.kosuri.stores.handler.StoreLicenceInfoHandler;
import com.kosuri.stores.model.request.DiagnosticCenterRequest;
import com.kosuri.stores.model.request.StoreLicenceInfoRequest;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.GetAllDiagnosticCentersResponse;
import io.micrometer.observation.ObservationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/storeLicenceInfo")
public class StoreLicenceInfoController {

    @Autowired
    private StoreLicenceInfoHandler storeLicenceInfoHandler;

    @PostMapping("/addStoreLicenceInfo")
    public ResponseEntity<GenericResponse> addStoreLicenceInfo(@RequestBody StoreLicenceInfoRequest request)  throws APIException{
        HttpStatus httpStatus;
        GenericResponse response = new GenericResponse();
        try {
            response =  storeLicenceInfoHandler.addStoreLicenceInfo(request);
            httpStatus = HttpStatus.OK;
        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(response);
    }

    @PutMapping("/updateStoreLicenceInfo")
    public ResponseEntity<GenericResponse> updateStoreLicenceInfo(@RequestBody StoreLicenceInfoRequest request) {
        HttpStatus httpStatus;
        GenericResponse response = new GenericResponse();
        boolean isStoreLicenceInfoUpdated = false;
        try {
            isStoreLicenceInfoUpdated = storeLicenceInfoHandler.updateStoreLicenceInfo(request);
            httpStatus = HttpStatus.OK;
            if (isStoreLicenceInfoUpdated){
                response.setResponseMessage("Store Licence Info updated successfully");
            } else{
                response.setResponseMessage("Store Licence Info Cannot Be Updated");
            }

        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(response);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreLicenceInfoEntity> getStoreLicenceInfo(@PathVariable String storeId) {
        HttpStatus httpStatus;
        GenericResponse response = new GenericResponse();
        StoreLicenceInfoEntity storeLicenceInfoEntity = new StoreLicenceInfoEntity();
        try{
            storeLicenceInfoEntity = storeLicenceInfoHandler.getStoreLicenceInfo(storeId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(storeLicenceInfoEntity);
    }

    @GetMapping
    public ResponseEntity<List<StoreLicenceInfoEntity>> getAllStoreLicenceInfo() {
        HttpStatus httpStatus;
        GenericResponse response = new GenericResponse();
        List<StoreLicenceInfoEntity> storeLicenceInfoEntity = new ArrayList<>();
        try{
            storeLicenceInfoEntity = storeLicenceInfoHandler.getAllStoreLicenceInfo();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(storeLicenceInfoEntity);
    }




}
