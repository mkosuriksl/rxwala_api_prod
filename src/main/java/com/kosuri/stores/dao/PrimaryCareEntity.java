package com.kosuri.stores.dao;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "primary_care")
public class PrimaryCareEntity {

	@Id
	private @Column(name = "userId_StoreId_ServiceId") String userIdStoreIdServiceId;
	
	@Nonnull
	private @Column(name = "service_id") String serviceId;
	private @Column(name = "service_name") String serviceName;
	private @Column(name = "price") double price;
	private @Column(name = "description") String description;
	private @Column(name = "user_iD") String userId;
	private @Column(name = "store_id") String storeId;
	private @Column(name = "service_category") String serviceCategory;
	private @Column(name = "updated_by") String updatedBy;
	private @Column(name = "status") String status;
	private @Column(name = "Amount_updated_date") LocalDateTime amountUpdatedDate;
	private @Column(name = "status_updated_date") LocalDateTime statusUpdatedDate;
	private @Column(name = "userId_StoreId") String userIdStoreId;

}
