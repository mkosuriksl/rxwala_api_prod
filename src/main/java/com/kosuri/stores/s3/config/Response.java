package com.kosuri.stores.s3.config;

public class Response {

	private ResponseService.ErrorCode code;
	private String message;
	private String exception;

	public Response(ResponseService.ErrorCode code, String message, String exception) {
		this.code = code;
		this.message = message;
		this.exception = exception;
	}

	public Response(ResponseService.ErrorCode code, String message) {
		this.code = code;
		this.message = message;
	}

	public Response() {
	}

	public ResponseService.ErrorCode getCode() {
		return code;
	}

	public void setCode(ResponseService.ErrorCode code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
}
