package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.HcServiceCategory;

import lombok.Data;

@Data
public class ResponseHcServiceCategoryGetDto {

	private String message;
	private boolean status;
	private List<HcServiceCategory> data;
}

