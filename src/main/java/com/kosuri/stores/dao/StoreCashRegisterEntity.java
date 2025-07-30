package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "store_cash_register")
public class StoreCashRegisterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime date;

	@Column(name = "sale_amount")
	private Double saleAmount;

	@Column(name = "return_amount")
	private Double returnAmount;

	@Column(name = "net_amount")
	private Double netAmount;

	@Column(name = "online_pay")
	private Double onlinePay;

	@Column(name = "cash_payment")
	private Double cashPayment;

	@Column(name = "cash_handover_amount")
	private Double cashHandoverAmount;

	@Column(name = "handed_over_by")
	private String handedOverBy;

	@Column(name = "accepted_by")
	private String acceptedBy;

	@Column(name = "cash_in_counter")
	private Double cashInCounter;

	@Column(name = "store_id")
	private String storeId;

}
