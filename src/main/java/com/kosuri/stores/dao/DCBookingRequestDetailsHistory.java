package com.kosuri.stores.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "pc_bookingRequest_details_history_service")
public class DCBookingRequestDetailsHistory {

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
	private int discount;

	@Column(name = "service_name")
	private String serviceName;
}
