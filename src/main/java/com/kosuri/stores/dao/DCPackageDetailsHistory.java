package com.kosuri.stores.dao;

import java.time.LocalDateTime;

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
@Table(name = "dc_package_details_history")
public class DCPackageDetailsHistory {

	@Id
	@Column(name = "packageid_lineid")
	private String packageIdLineId;
	
	@Column(name = "package_id")
	private String packageId;

	@Column(name = "amount")
	private double amount;
	
	@Column(name = "discount")
	private int discount;
	
	@Column(name = "serviceid")
	private String serviceId;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
	
	@Column(name = "serviceName")
	private String serviceName;//


}
