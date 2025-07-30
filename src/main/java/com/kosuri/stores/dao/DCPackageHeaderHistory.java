package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name = "dc_package_header_history")
public class DCPackageHeaderHistory {

	@Id
	@Column(name = "package_id")
	private String packageId;
	
	@Column(name = "userId")
	private String userId;

	@Column(name = "storeid")
	private String storeId;

	@Column(name = "total_amount")
	private double totalAmount;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
	
	@Column(name = "userId_storeid")
	private String userIdStoreId;
	
	@Column(name = "package_name")
	private String packageName;
	
	@PrePersist
	private void prePersist() {
		this.userIdStoreId = userId + "_" + storeId;
	}

	@PreUpdate
	private void preUpdate() {
		this.userIdStoreId = userId + "_" + storeId;
	}
	
}
