package com.kosuri.stores.dao;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "patient_diagnostic")
public class PatientDiagnostic {

	@Id
	@Column(name = "visit_ord_no", nullable = false)
	private String visitOrdNo;

	@Column(name = "Store_ID")
	private String diagnosticStoreId;

	@Column(name = "Updated_By")
	private String updatedBy;

	@Column(name = "Updated_Date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

}
