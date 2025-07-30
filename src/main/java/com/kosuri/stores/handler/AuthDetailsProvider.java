package com.kosuri.stores.handler;

import com.kosuri.stores.config.JwtService;

public class AuthDetailsProvider {
	public static String getLoggedEmail() {
		return JwtService.CURRENT_USER;
	}
}
