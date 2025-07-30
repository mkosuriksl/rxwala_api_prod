package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.PurchaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseUpdateFinalResponse {
	private String invoiceNo;
	private List<PurchaseEntity> updated;
	private String message;
	private String status;

}
