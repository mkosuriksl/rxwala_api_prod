package com.kosuri.stores.model.dto;

import com.kosuri.stores.dao.HcServiceCategory;

import lombok.Data;

@Data
public class ResponseHcServiceCategoryDto {

	private String message;
	private boolean status;
	private HcServiceCategory data;
}

