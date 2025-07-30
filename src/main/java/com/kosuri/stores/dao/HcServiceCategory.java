package com.kosuri.stores.dao;

import java.time.LocalDate;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hc_service_category")
public class HcServiceCategory {
	
	@Id
	@Column(name = "userid_storeid_servicecategoryid")
	private String userIdStoreIdServicecategoryId;
	
	
	@Column(name = "service_category_id")
	private String serviceCategoryId;
	
	@Column(name = "service_category_name")
	private String serviceCategoryName;
	
	@UpdateTimestamp
	@Column(name="updated_date")
	private LocalDate updatedDate;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="store_id")
	private String storeId;
	
	@Column(name="status")
	private String status;
	
//	@UpdateTimestamp
	@Column(name="status_updated_date")
	private LocalDate statusUpdatedDate;
	
	@Column(name="status_updated_by")
	private String statusUpdatedBy;
	
	@PrePersist
	private void prePersist() {
		this.userIdStoreIdServicecategoryId = updatedBy+"_"+storeId+"_" +serviceCategoryId;
	}
	
}

