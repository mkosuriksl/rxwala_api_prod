package com.kosuri.stores.dao;

import java.time.LocalDate;
import java.util.Random;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "patient_visit_history")
public class PatientVisitHistoryEntity {

	@Id
	@Column(name = "visit_ord_no", nullable = false)
	private String visitOrdNo;

//	@ManyToOne
//	@JoinColumn(name = "cid", nullable = false)
//	private CustomerRegisterEntity customerRegisterEntity;
	
	@Column(name = "cid", nullable = false)
	private String cId;

	@Column(name = "visiting_date")
	private LocalDate visitingDate;

	@Column(name = "cause_of_visit", length = 300)
	private String causeOfVisit;

	@Column(name = "medication", length = 300)
	private String medication;

	@Column(name = "treated_by", length = 45)
	private String treatedBy;

	@Column(name = "referred_to", length = 45)
	private String referredTo;
	
	@PrePersist
    public void generateVisitOrdNo() {
        if (this.visitOrdNo == null) {
            this.visitOrdNo = "VI" + String.format("%05d", new Random().nextInt(100000));
        }
    }
}
