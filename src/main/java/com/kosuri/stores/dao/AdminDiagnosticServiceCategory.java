package com.kosuri.stores.dao;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "admin_diagnostic_service_category")
public class AdminDiagnosticServiceCategory {

	@Id
	@Column(name = "dc_service_category_id", unique = true, nullable = false)
	private String dcServiceCategoryId;

	@Column(name = "dc_service_category_name")
	private String dcServiceCategoryName;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDate updatedDate;
	
//	@PrePersist
//	private void prePersist() {
//		this.dcServiceCategoryId= "DC"+"_";
//	}
	

}
