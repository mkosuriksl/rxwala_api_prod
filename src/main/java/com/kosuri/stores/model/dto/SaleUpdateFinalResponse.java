package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.SaleEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleUpdateFinalResponse {
	private String doc_Number;
	private List<SaleEntity> updated;
	private String message;
	private String status;

}
