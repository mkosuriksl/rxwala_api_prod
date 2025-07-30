package com.kosuri.stores.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kosuri.stores.config.JwtService;

public class CurrentUser {

	public static String getEmail() {
		String input = JwtService.CURRENT_USER;
		// Define a regular expression for extracting email addresses
		String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(input);

		// Find the email address
		if (matcher.find()) {
			return matcher.group();
		} else {
			return null;
		}
	}

}
