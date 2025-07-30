package com.kosuri.stores.dao;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "store_admin_brands")
public class StoreAdminBrandEntity {

	@Id
	@Column(name = "admin_id_brand_id")
	private String id;

	@Column(name = "brand_name")
	private String brandName;

	@Column(name = "brand_id")
	private String brandId;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDate updatedDate;

	@Column(name = "store_id")
	private String storeId;

	@Column(name = "item_category")
	private String itemCategory;

	@Column(name = "item_subcategory")
	private String itemSubcategory;

}
