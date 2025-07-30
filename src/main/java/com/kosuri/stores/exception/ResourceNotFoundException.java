package com.kosuri.stores.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	private String message;

	public ResourceNotFoundException(String message) {
		super(String.format(message));

		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
