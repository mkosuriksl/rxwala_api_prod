package com.kosuri.stores.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "sales_pharma_header")
public class SaleHeaderEntity {

	@Id
	@Column(name = "Doc_No", nullable = false, unique = true)
	private String docNumber;

	@Column(name = "Date")
	private Date date;

	@Column(name = "Cust_Code", nullable = true, length = 45)
	private String custCode;

	@Column(name = "Cust_Name", nullable = true, length = 45)
	private String custName;

	@Column(name = "Store_ID", nullable = true, length = 45)
	private String storeId;

	@Column(name = "UserIdStoreId")
	private String userIdStoreId;

	@Column(name = "Taxable_Amt")
	private Double taxableAmt;

	@Column(name = "Cgst_Amt")
	private Double cGSTAmt;

	@Column(name = "Sgst_Amt")
	private Double sGSTAmt;

	@Column(name = "Igst_Amt")
	private Double iGSTAmt;

	@Column(name = "Cess_Amt")
	private Double cessAmt;

	@Column(name = "Total")
	private Double total;

}