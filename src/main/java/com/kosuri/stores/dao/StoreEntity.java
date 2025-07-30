package com.kosuri.stores.dao;

import java.time.LocalDate;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@ToString
@Table(name = "store_info")
public class StoreEntity {
	private @Column(name = "Store_Category") String type;

	@Id
	private @Column(name = "userId_storeId", nullable = false) String userIdStoreId;

	private @Column(name = "Store_ID") String id;
	private @Column(name = "Store_Name") String name;
	private @Column(name = "Pincode") String pincode;
	private @Column(name = "Village") String district;
	private @Column(name = "State") String state;
	private @Column(name = "Location") String location;
	private @Column(name = "Contact_Person_Name") String owner;
	private @Column(name = "Phone_Number") String ownerContact;
	private @Column(name = "Secondary_Phone") String secondaryContact;
	private @Column(name = "EmailId") String ownerEmail;
	private @Column(name = "Reg_Date") LocalDate registrationDate;
	private @Column(name = "Time_Stamp") String creationTimeStamp;
	private @Column(name = "Role") String role;
	private @Column(name = "Added_By") String addedBy;
	private @Column(name = "Modified_By") String modifiedBy;
	private @Column(name = "Modified_Date") String modifiedDate;
	private @Column(name = "Modified_Time_Stamp") String modifiedTimeStamp;
	private @Column(name = "Status") String status;
	private @Column(name = "store_verified_status") String storeVerifiedStatus;
	private @Column(name = "`expiry date`") String expiryDate;
	private @Column(name = "current_plan") String currentPlan;
	private @Column(name = "store_business_type") String storeBusinessType;
	private @Column(name = "userId") String userId;
	private @Column(name = "gstNumber") String gstNumber;

	@PrePersist
	private void prePersist() {
		this.userIdStoreId = userId + "_" + id;
	}
}