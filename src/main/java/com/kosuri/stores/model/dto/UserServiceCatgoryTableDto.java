package com.kosuri.stores.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceCatgoryTableDto {
	
	private Long Id;

	private String userId;

	private List<String> serviceCategories;

	private String dashboardRole;

	private String updatedBy;

	private LocalDateTime updatedDate;

}
