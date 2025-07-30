package com.kosuri.stores.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class StoreWithPrimaryCareResponseDto {

	private List<StoreDto> stores;
	@JsonIgnore
	private List<PrimaryCareDto> primaryCare;
}
