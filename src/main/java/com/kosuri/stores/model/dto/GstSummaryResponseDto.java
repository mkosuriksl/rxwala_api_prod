package com.kosuri.stores.model.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class GstSummaryResponseDto {
	private Integer gstCode;
	private BigDecimal taxableAmount = BigDecimal.ZERO;
	private BigDecimal cGSTAmt = BigDecimal.ZERO;
	private BigDecimal sGSTAmt = BigDecimal.ZERO;
	private BigDecimal total = BigDecimal.ZERO;
	private Date date;
	private String storeId;
	private String userIdStoreId;  
	private String userId;    
}
