package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.kosuri.stores.model.enums.Status;

import lombok.Data;

@Data
public class GetBookingResponse {
	private String serviceRequestId;
    private String customerId;
    private String userIdStoreId;
    private Boolean homeService;
    private Boolean walkinService;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private LocalDate updatedDate;
    private String updatedBy;
    private LocalDate bookingDate;
    private double bookingTotal;
    private Status status;
    private List<PackageResponse> selectedPackages;
    private StoreDetailsDto storeDetails;
}
