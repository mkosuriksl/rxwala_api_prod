package com.kosuri.stores.model.dto;

import com.kosuri.stores.dao.AddAmbulance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseModel {

	private String error;
	private String msg;
	private AddAmbulance ambulance;
	public ResponseModel() {

	}
	public ResponseModel(String error, String msg, AddAmbulance ambulance) {
		super();
		this.error = error;
		this.msg = msg;
		this.ambulance = ambulance;
	}
	
	public ResponseModel(String message, String error) {
		this.error = error;
		this.msg = message;
	}
	
	public ResponseModel(String msg) {
		this.msg = msg;
	}

}
