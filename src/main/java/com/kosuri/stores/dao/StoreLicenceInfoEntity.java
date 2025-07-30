package com.kosuri.stores.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "store_license_info")
public class StoreLicenceInfoEntity {

	@Id
	@Column(name = "store_id", nullable = false, length = 10)
	private String storeId;

	@Column(name = "pharmacy_license", length = 45)
	private String pharmacyLicense;

	@Column(name = "gst_license", length = 45)
	private String gstLicense;

	@Column(name = "licence_number", length = 45)
	private String licenceNumber;

	@Column(name = "gst_number", length = 45)
	private String gstNumber;

	@Column(name = "pharmacy_license_expiry", length = 45)
	private String pharmacyLicenseExpiry;

	@Column(name = "updated_by", length = 45)
	private String updatedBy;

	@Column(name = "updated_date", length = 45)
	private String updatedDate;

	@Column(name = "license_registered_state", length = 45)
	private String licenseRegisteredState;

	@Column(name = "license_registered_district", length = 45)
	private String licenseRegisteredDistrict;

	@Column(name = "license_registered_division", length = 45)
	private String licenseRegisteredDivision;
}
