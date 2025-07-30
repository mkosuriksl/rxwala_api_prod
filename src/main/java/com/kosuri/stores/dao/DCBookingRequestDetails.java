package com.kosuri.stores.dao;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kosuri.stores.model.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pc_bookingRequest_details_service")
public class DCBookingRequestDetails {

	@Id
	@Column(name = "service_request_line_id")
	private String serviceRequestLineId;

	@Column(name = "service_request_id")
	private String serviceRequestId;
	
	@Column(name = "service_id")
	private String serviceId;

	@Column(name = "amount")
	private double amount;

	@Column(name = "discount")
	private Double discount;
	
	@Column(name = "service_name")
	private String serviceName;
	
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

}
