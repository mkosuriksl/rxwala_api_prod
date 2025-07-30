package com.kosuri.stores.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CustomerOrderDetailsDto {
	private String orderlineId;
	private String itemCode;
	private String storeId;
	private String itemName;
	private Double  mrp;
	private Integer discount;
	private Integer gst;
	private Double total;
	private String manufacturerName;
	private Integer orderQty;
	private String prescriptionRequired;
	private Date updatedDate;
	private String updatedBy;
	private String userIdStoreIdItemCode;
	private String userId;

}
