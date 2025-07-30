package com.kosuri.stores.dao;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "pc_bookingRequest_header_history_service")
public class DCBookingRequestHeaderHistory {

	@Id
	@Column(name = "service_request_id")
	private String serviceRequestId;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "userId_storeId")
	private String userIdStoreId;

	@Column(name = "total_amount")
	private double totalAmount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	@Column(name = "appointment_date")
	private LocalDate appointmentDate;

	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	@Column(name = "appointment_time")
	private LocalTime appointmentTime;

	@Column(name = "home_service")
	private String homeService;

	@Column(name = "walkin_service")
	private String walkinService;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDate updatedDate;

	@Column(name = "booking_date")
	private LocalDate bookingDate;
	
	@Column(name = "status")
	private String status;
}
