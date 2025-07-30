package com.kosuri.stores.dao;

import java.time.LocalDate;
import java.time.LocalTime;

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
@Table(name = "dc_booking_header01")
public class DcBookingHeader {

	@Id
	@Column(name = "serviceReques_id")
	private String serviceRequestId;

	@Column(name = "customer_id")
	private String customerId;
	
	@Column(name = "userid_storeid")
	private String userIdStoreId;

	@Column(name = "homeservice")
	private Boolean  homeService;

	@Column(name = "walkinservice")
	private Boolean  walkinService;

	@Column(name = "appointment_date")
	private LocalDate  appointmentDate;

	@Column(name = "appointment_time")
	private LocalTime   appointmentTime;

	@Column(name = "updated_date")
	private LocalDate  updatedDate;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "booking_date")
	private LocalDate bookingDate;
	
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;
    
    @Column(name = "booking_Total")
	private double bookingTotal;

}
