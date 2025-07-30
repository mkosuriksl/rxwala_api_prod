package com.kosuri.stores.dao;

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
@Table(name = "dc_booking_details01")
public class DcBookingDetails {

	@Id
	@Column(name = "serviceRequest_lineId")
	private String serviceRequestLineId;

	@Column(name = "serviceRequest_Id")
	private String serviceRequestId;
	
	@Column(name = "package_id")
	private String packageId;

	@Column(name = "package_name")
	private String  packageName;

	@Column(name = "total_amount")
	private double  totalAmount;

	@Column(name = "service_id")
	private String serviceID;
	
	@Column(name = "amount")
	private double  amount;
	
	@Column(name = "discount")
	private double  discount;
	
	@Column(name = "service_name")
	private String  serviceName;

	@Column(name = "packageId_lineid")
	private String   packageIdLineId;
	
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

}
