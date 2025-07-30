package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.kosuri.stores.model.enums.Status;

import lombok.Data;

@Data
public class GetBookingResponseCustomerInfo {
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
    private Status status;
    private double bookingTotal;
    private List<PackageResponse> selectedPackages;
    private CustomerDetailsDto customerDetailsDto;
    private StoreDetailsDto storeDetailsDto;
}
