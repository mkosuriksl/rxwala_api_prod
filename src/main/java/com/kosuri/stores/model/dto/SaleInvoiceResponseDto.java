package com.kosuri.stores.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleInvoiceResponseDto {
	private String doc_Number;
    private Date date;
    private String custName;
    private String storeId;
    private String userIdStoreId;
    
    
}
