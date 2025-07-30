package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dc_package_details1")
public class DCPackageDetails {

	@Id
	@Column(name = "packageid_lineid")
	private String packageIdLineId;

//	@Column(name = "userId_storeid")
//	private String userIdStoreId;

	@Column(name = "package_id")
	private String packageId;

	@Column(name = "amount")
	private double amount;

	@Column(name = "discount")
	private int discount;//

//	@Column(name = "package_name")
//	private String packageName;

	@Column(name = "serviceid")
	private String serviceId;//

	@Column(name = "serviceName")
	private String serviceName;//

	@Column(name = "updated_by")
	private String updatedBy;//

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Transient
	private String packageName;
	
	@Transient
	private String storeId;
	
	@Transient
	private String userId;

}
