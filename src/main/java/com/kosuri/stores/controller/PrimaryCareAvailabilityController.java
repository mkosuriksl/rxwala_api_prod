package com.kosuri.stores.controller;

import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.PrimaryCareHandler;
import com.kosuri.stores.model.request.PrimaryCareAvailabilityRequest;
import com.kosuri.stores.model.request.PrimaryCareUserRequest;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.GetAllPrimaryCareCentersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/primaryCareAvailability")
public class PrimaryCareAvailabilityController {
    @Autowired
    PrimaryCareHandler primaryCareHandler;


    @PostMapping("/addUpdateAvailability")
    public ResponseEntity<?> addOrUpdateAvailability(@RequestBody PrimaryCareAvailabilityRequest request) {
        primaryCareHandler.updatePrimaryCareAvailability(request);
        return ResponseEntity.ok().build();
    }

}




