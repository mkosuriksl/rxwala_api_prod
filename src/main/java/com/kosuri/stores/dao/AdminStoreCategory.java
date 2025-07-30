package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "admin_store_category")
public class AdminStoreCategory {
	
	@Id
	@Column(name = "store_category_id")
	private String storeCategoryId;

	@Column(name = "store_category")
	private String storeCategory;
	
	@Column(name = "status") 
	private String status;
	
	@Column(name = "status_updated_date") 
	private LocalDateTime statusUpdatedDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@UpdateTimestamp
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

}
