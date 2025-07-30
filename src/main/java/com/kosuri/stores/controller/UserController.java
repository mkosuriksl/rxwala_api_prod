package com.kosuri.stores.controller;

import com.kosuri.stores.constant.StoreConstants;
import com.kosuri.stores.model.request.*;
import com.kosuri.stores.model.response.CreateStoreResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.UserHandler;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.LoginUserResponse;
import com.kosuri.stores.model.response.OTPResponse;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserHandler userHandler;

	@PostMapping("/add")
	public ResponseEntity<GenericResponse> addUser(@RequestBody AddUserRequest request) {
		HttpStatus httpStatus;
		GenericResponse response = new GenericResponse();
		try {
			userHandler.addUser(request);
			httpStatus = HttpStatus.OK;
			response.setResponseMessage("User added successfully");
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest request) {
		HttpStatus httpStatus;
		LoginUserResponse response = new LoginUserResponse();
		String body;
		try {
			response = userHandler.loginUser(request);
			httpStatus = HttpStatus.OK;
			response.setResponseMessage("User logged in successfully!");
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/register")
	public ResponseEntity<CreateStoreResponse> addUser(@RequestBody AddTabStoreUserRequest request) {
		HttpStatus httpStatus;
		CreateStoreResponse response = new CreateStoreResponse();
		try {
			response = userHandler.addUser(request);
			httpStatus = HttpStatus.OK;
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage("Error While Adding Store User");
		}
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/changePassword")
	public ResponseEntity<GenericResponse> changePassword(@RequestBody PasswordRequest request) {
		HttpStatus httpStatus;
		GenericResponse response = new GenericResponse();
		try {
			if (request.getIsForgetPassword()) {
				response = userHandler.forgetPassword(request);

			} else {
				response = userHandler.changePassword(request, false);
			}
			httpStatus = HttpStatus.OK;

		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/verifyForgetPassword")
	public ResponseEntity<GenericResponse> verifyOTPAndChangePassword(@RequestBody PasswordRequest request) {
		HttpStatus httpStatus;
		GenericResponse response = new GenericResponse();
		try {
			response = userHandler.verifyOTPAndChangePassword(request);
			httpStatus = HttpStatus.OK;

		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/sendEmailOtp")
	public ResponseEntity<OTPResponse> sendEmailOTP(@RequestBody OTPRequest request) {
		HttpStatus httpStatus;
		OTPResponse response = new OTPResponse();
		try {
			boolean isOtpSend = userHandler.sendEmailOtp(request);
			httpStatus = HttpStatus.OK;
			if (isOtpSend) {
				response.setOtp("OTP Send To Email Successfully");
			} else {
				response.setOtp("OTP Send Failed.Please Check Email");
			}
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setOtp(e.getMessage());
		}
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/sendPhoneOtp")
	public ResponseEntity<OTPResponse> sendPhoneOTP(@RequestBody OTPRequest request) {
		HttpStatus httpStatus;
		OTPResponse response = new OTPResponse();
		try {
			boolean isOtpSend = userHandler.sendOTPToPhone(request);
			httpStatus = HttpStatus.OK;
			if (isOtpSend) {
				response.setOtp("OTP Send To Phone Successfully");
			} else {
				response.setOtp("OTP Send Failed.Please Check Phone");
			}
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setOtp(e.getMessage());
		}
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/verifyEmailOTP")
	public ResponseEntity<GenericResponse> verifyEmailOTP(@RequestBody VerifyOTPRequest emailOtp) throws APIException {
		HttpStatus httpStatus;
		GenericResponse response = new GenericResponse();
		try {
			emailOtp.setIsForgetPassword(false);
			boolean isEmailVerified = userHandler.verifyEmailOTP(emailOtp);
			httpStatus = HttpStatus.OK;
			if (isEmailVerified) {
				response.setResponseMessage("Email Verification Success");
			} else if (StoreConstants.IS_EMAIL_ALREADY_VERIFIED) {
				response.setResponseMessage("User Already Verified");
			}
		} catch (APIException e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			response.setResponseMessage(e.getMessage());
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage("Verification failed.");
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/rxwala-storeowner-delete-account")
	public ResponseEntity<GenericResponse> servicePersonDeletAccoutn(@RequestBody DeleteAccountRequest accountRequest) {
		return new ResponseEntity<GenericResponse>(userHandler.servicePersonDeletAccout(accountRequest),
				HttpStatus.CREATED);
	}

	@PostMapping("/verifySmsOTP")
	public ResponseEntity<GenericResponse> verifySMSOTP(@RequestBody VerifyOTPRequest smsOtp) {
		HttpStatus httpStatus;
		GenericResponse response = new GenericResponse();
		try {
			smsOtp.setIsForgetPassword(false);
			boolean isEmailVerified = userHandler.verifySmsOTP(smsOtp);
			httpStatus = HttpStatus.OK;
			if (isEmailVerified) {
				response.setResponseMessage("SMS Verification Success");
			} else if (StoreConstants.IS_EMAIL_ALREADY_VERIFIED) {
				response.setResponseMessage("User Already Verified");
			} else {
				response.setResponseMessage("Invalid Otp");
			}
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setResponseMessage(e.getMessage());
		}

		return ResponseEntity.status(httpStatus).body(response);
	}
}
