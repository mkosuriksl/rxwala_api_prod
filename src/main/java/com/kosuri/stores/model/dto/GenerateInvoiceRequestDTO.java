package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class GenerateInvoiceRequestDTO {
	private String invNumber;

	private String ponumber;

	private Double amount;

	private String status;

	private String updatedBy;
}
