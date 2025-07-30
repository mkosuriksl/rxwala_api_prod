package com.kosuri.stores.dao;

import java.util.Date;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "walk_in_customer_register")
public class WalkInCustomerEntity {

	@Id
	@Column(name = "cid", unique = true, nullable = false)
	private String cId;
	
	private String storeId;

	@UpdateTimestamp
	private Date updatedDate;
	private String updatedBy;

}
