package com.kosuri.stores.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeForgotdto {
	private String email;
	private String mobileNo;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;
	private String otp;
}
