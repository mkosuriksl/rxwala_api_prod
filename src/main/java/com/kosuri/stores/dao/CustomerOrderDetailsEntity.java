package com.kosuri.stores.dao;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_order_method_details")
public class CustomerOrderDetailsEntity {
	@Id
	@Column(name = "Orderline_Id")
	private String orderlineId;

	@Column(name = "store_id")
	private String storeId;

	@Column(name = "item_code")
	private String itemCode;

	@Column(name = "item_name")
	private String itemName;

	@Column(name = "mrp")
	private Double  mrp;
	
	@Column(name = "discount")
	private Integer  discount;

	@Column(name = "gst")
	private Integer gst;

	@Column(name = "total")
	private Double total;

	@Column(name = "manufacturer_name")
	private String manufacturerName;

	@Column(name = "orderQty")
	private Integer orderQty;

	@Column(name = "prescriptionRequired")
	private String prescriptionRequired;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "updatedBy")
	private String updatedBy;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "orderId", referencedColumnName = "orderId")
	private CustomerOrderHdrEntity customerOrderOrderHdrEntity;
	
	private @Column(name = "UserIdStoreId_ItemCode") String userIdStoreIdItemCode;	

	@Column(name = "userid")
	private String userId;
	
	@Column(name = "userid_storeid")
	private String userIdStoreId;
	
}
