package com.kosuri.stores.model.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminStoreCategoryDto {
	
	private String storeCategoryId;

	private String storeCategory;

	private String status;
	
	private LocalDateTime statusUpdatedDate;

	private String updatedBy;

	private LocalDateTime updatedDate;


}
