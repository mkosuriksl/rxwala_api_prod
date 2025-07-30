package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.kosuri.stores.model.enums.Status;

import lombok.Data;

@Data
public class AppintmentBookingRequestdc {
	private String serviceRequestId;
    private String customerId;
    private String userIdStoreId;
    private Boolean homeService;
    private Boolean walkinService;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Status status;
}
