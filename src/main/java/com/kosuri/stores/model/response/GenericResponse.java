package com.kosuri.stores.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenericResponse {

	private String responseMessage;
	
	private Object details;
	
	private boolean status;
}
