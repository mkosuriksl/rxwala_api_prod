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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "diagnostic_service_packagid_line")
public class DiagnosticServicePackageLine {

	@Id
	@Column(name = "storeid_packageid_line_id", nullable = false)
	private String lineId;// userid_storeid_serviceID+3digit number starting with 001

	@Column(name = "service_category_id")
	private String serviceCategoryId;

	@Column(name = "store_id")
	private String storeId;

	@Column(name = "package_id")
	private String packageId;

	@Column(name = "package_name")
	private String packageName;

	@Column(name = "service_id")
	private String serviceId;

	@Column(name = "amount")
	private Double amount;

	@Column(name = "discount")
	private int discount;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

}
