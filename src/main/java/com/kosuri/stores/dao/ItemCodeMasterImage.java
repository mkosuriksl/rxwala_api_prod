package com.kosuri.stores.dao;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
@Table(name = "item_code_master_image")
public class ItemCodeMasterImage {

	@Id
	@Column(name = "UserIdStoreId_ItemCode", unique = true, nullable = false)
	private String userIdStoreIdItemCode;
	
	@Column(name = "image1", length = 255)
	private String image1;
	
	@Column(name = "image2", length = 255)
	private String image2;
	
	@Column(name = "image3", length = 255)
	private String image3;

	@Column(name = "Updated_By")
	private String updatedBy;

	@Column(name = "Updated_Date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
}
