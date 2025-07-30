package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.kosuri.stores.model.enums.Status;

import lombok.Data;

@Data
public class AppintmentBookingRequest {
	private String serviceRequestId;
    private String customerId;
    private String userIdStoreId;
    private String homeService;
    private String walkinService;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Status status;
}
