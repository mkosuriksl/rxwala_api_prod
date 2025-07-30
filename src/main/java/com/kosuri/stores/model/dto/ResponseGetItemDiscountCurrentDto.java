package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.ItemDiscountCurrent;

import lombok.Data;

@Data
public class ResponseGetItemDiscountCurrentDto {

	private String message;
	private boolean status;
	private List<ItemDiscountCurrent> data;
}
