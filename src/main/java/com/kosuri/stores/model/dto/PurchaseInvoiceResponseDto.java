package com.kosuri.stores.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseInvoiceResponseDto {
	private String invoiceNo;
    private Date date;
    private String suppName;
    private String storeId;
    private String userIdStoreId;
    
    
}
