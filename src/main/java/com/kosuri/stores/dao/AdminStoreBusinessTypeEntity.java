package com.kosuri.stores.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@ToString
@Table(name = "admin_store_business_type")
public class AdminStoreBusinessTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private @Column(name = "id") Integer id;
	
	@Column(name = "business_type_id", unique = true, nullable = false)
	private  String businessTypeId;
	private @Column(name = "business_name") String businessName;
	private @Column(name = "updated_date") LocalDateTime updatedDate;
	private @Column(name = "updated_by") String updatedBy;

}
