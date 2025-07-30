package com.kosuri.stores.dao;

import java.time.LocalDate;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "item_discount_history")
public class ItemDiscountHistory {

	@Id
	@Column(name = "userId_storeId_itemCode")
	private String userIdStoreIdItemCode;
	
	@Column(name = "Item_Code")
	private String itemCode;

	@Column(name = "userId_storeid")
	private String userIdStoreId;

	@Column(name = "discount")
	private int discount;

	@Column(name = "updated_by")
	private String updatedBy;

	@UpdateTimestamp
	@Column(name="updated_date")
	private LocalDate updatedDate;

	@PrePersist
	private void prePersist() {
		this.userIdStoreIdItemCode = userIdStoreId + "_" + itemCode;
	}

}
