package com.kosuri.stores.model.response;

import java.util.List;

import com.kosuri.stores.model.dto.PurchaseInvoiceResponseDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ResponseGetPurchaseInvoiceDto {
	private String message;
	private boolean status;
    private List<PurchaseInvoiceResponseDto> purchase;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;

}
