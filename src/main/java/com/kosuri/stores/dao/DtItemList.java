package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "dt_item_list")
public class DtItemList {
	
	@Id
	@Column(name = "item_code")
	private String itemCode;

	@Column(name = "item_category")
	private String itemCategory;

	@Column(name = "item_subcategory")
	private String itemSubcategory;

	@Column(name = "brand")
	private String brand;

	@Column(name = "manufacturer ")
	private String manufacturer;

	@Column(name = "gst")
	private Integer gst;

	@Column(name = "updated_by")
	private String updatedBy;

	@UpdateTimestamp
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "userId_ItemCode")
	private String userIdItemCode;

	@Column(name = "item_name")
	private String itemname;

}
