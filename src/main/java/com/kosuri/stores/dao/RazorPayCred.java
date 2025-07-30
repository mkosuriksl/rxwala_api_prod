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

@Entity
@Table(name = "razor_payment_cred")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RazorPayCred {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "key_detail", length = 50)
	private String key;

	@Column(name = "key_sercret", length = 50)
	private String keySercret;

	@Column(name = "currency", length = 50)
	private String currency;

	private LocalDateTime createdDate;

}

