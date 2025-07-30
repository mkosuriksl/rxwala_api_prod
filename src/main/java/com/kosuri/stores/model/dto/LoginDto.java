package com.kosuri.stores.model.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginDto {

	private String email;
	private String mobileNo;
	private String password;

}
