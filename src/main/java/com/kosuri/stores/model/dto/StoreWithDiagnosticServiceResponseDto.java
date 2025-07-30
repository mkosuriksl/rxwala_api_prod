package com.kosuri.stores.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class StoreWithDiagnosticServiceResponseDto {

	private List<StoreDto> stores;
	@JsonIgnore
	private List<DiagnosticServiceDto> diagnosticServices;
	@JsonIgnore
	private List<DCPackageHeaderDto> dcPackageHeaders;
}
