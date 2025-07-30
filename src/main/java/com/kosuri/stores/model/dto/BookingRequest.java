package com.kosuri.stores.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.kosuri.stores.model.enums.Status;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {

	private String serviceRequestId;
    private String customerId;
    private String userIdStoreId;
    private Boolean homeService;
    private Boolean walkinService;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private double bookingTotal;
    private Status status;
    private List<PackageRequest> selectedPackages;
}
