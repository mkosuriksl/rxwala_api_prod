package com.kosuri.stores.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "primary_care_availability_latest")
public class PrimaryCareAvailabilityLatestEntity {

	@Id
	@Column(name = "provider_id")
	private String providerId;

	@Column(name = "primary_care_avail_loc")
	private String primaryCareAvailLoc;

	@Column(name = "updated_date")
	private LocalDate updatedDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "availability")
	private String availability;
}
