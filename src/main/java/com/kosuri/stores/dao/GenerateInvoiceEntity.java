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
@Table(name = "generate_invoice")
public class GenerateInvoiceEntity {

	@Id
	@Column(name = "invnumber")
	private String invNumber;

	@Column(name = "ponumber")
	private String ponumber;

	@Column(name = "amount")
	private Double amount;

	@Column(name = "status")
	private String status;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

}
