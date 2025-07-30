package com.kosuri.stores.dao;

import java.util.Date;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "store_and_store_user")
public class StoreAndStoreUserEntity {

	@Id
	@Nonnull
	private @Column(name = "su_userId") String suUserId;
	private @Column(name = "userid_storeid") String userIdstoreId;
	private @Column(name = "storeid") String storeId;
	private @Column(name = "updatedBy") String updatedBy;
	@UpdateTimestamp
	private @Column(name = "Date")Date updatedDate;


}
