package com.kosuri.stores.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.CustomerRegistrationInfoDto;
import com.kosuri.stores.dao.CustomerWalkInUpdateRequest;
import com.kosuri.stores.dao.ResponseGetCustomerRegistrationDto;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.CustomerHandler;
import com.kosuri.stores.handler.OtpHandler;
import com.kosuri.stores.model.dto.ChangeForgotdto;
import com.kosuri.stores.model.dto.CustomerRegisterDto;
import com.kosuri.stores.model.dto.CustomerUpdateRequest;
import com.kosuri.stores.model.dto.ResponseCustomerLoginDto;
import com.kosuri.stores.model.dto.ResponseMessageDto;
import com.kosuri.stores.model.dto.WalkInCustomerRequestDto;
import com.kosuri.stores.model.request.DeleteAccountRequest;
import com.kosuri.stores.model.request.LoginUserRequest;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.LoginUserResponse;

import jakarta.transaction.Transactional;

@RestController
public class CustomerRegisterController {

	@Autowired
	private CustomerHandler customerHandler;

	@Autowired
	private CustomerRegisterRepository registerRepository;

	@Autowired
	private OtpHandler otpHandler;
	
	

//    @PostMapping("/customerRegister")
//    public ResponseEntity<CustomerRegisterDto>  addCustomerRegister(@RequestBody CustomerRegisterDto customerRegisterDto){
//        return new ResponseEntity<>(customerHandler.customerRegister(customerRegisterDto), HttpStatus.CREATED);
//    }

    @GetMapping("/getDetailsByEmail")
    public ResponseEntity<CustomerRegisterDto> getCustomerDetails(@RequestParam String email){
        return new ResponseEntity<>(customerHandler.getByEmail(email),HttpStatus.OK);
    }

	@PostMapping("/customerRegister")
	public ResponseEntity<?> addCustomerRegistration(@RequestBody CustomerRegisterEntity dlerBusinessl) {
		ResponseCustomerLoginDto response = new ResponseCustomerLoginDto();
		try {
			CustomerRegisterDto dblDTO = customerHandler.addCustomerRegistration(dlerBusinessl);
			if (dblDTO != null) {

				response.setMessage("Customer added successfully");
				response.setStatus(true);
				response.setDlerProfile(dblDTO);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("profile already exists");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			response.setMessage("failed to add");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	@PostMapping("/rxwala-send-otp-verify-email")
	public ResponseEntity<?> sendEmail(@RequestBody CustomerRegisterEntity login) {
		String emailId = login.getEmail();
		ResponseMessageDto message = new ResponseMessageDto();
		try {
			if (customerHandler.isEmailExists(emailId) == null) {
				message.setMessage("Invalid Email");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else {
				Optional<CustomerRegisterEntity> user = registerRepository.findByEmail(emailId);

				if (!user.get().getEmailVerify().equals("yes")) {
					otpHandler.generateOtp(emailId, "registration");
					message.setMessage("OTP Sent to Registered EmailId");
					message.setStatus(true);
					return new ResponseEntity<>(message, HttpStatus.OK);
				}
				message.setMessage("Email already verified");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}
	}

	@PostMapping("/rxwala-verify-otp-verify-email")
	public ResponseEntity<?> verifyUserEmail(@RequestBody CustomerRegisterEntity login) {
		String email = login.getEmail();
		String emailOtp = login.getEmailOtp();
		ResponseMessageDto message = new ResponseMessageDto();
		try {
			if (otpHandler.verifyOtp(email, emailOtp)) {

				customerHandler.updateData(emailOtp, email);
				message.setStatus(true);
				message.setMessage("Email Verified successful");
				return new ResponseEntity<>(message, HttpStatus.OK);
			}
			message.setMessage("Incorrect OTP, Please enter correct Otp");
			return new ResponseEntity<>(message, HttpStatus.OK);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}
	}
	
	@PostMapping("/rxwala-login-customerlogin")
	public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest request) {
		HttpStatus httpStatus;
		LoginUserResponse response = new LoginUserResponse();
		String body;
		try {
			response = customerHandler.loginCustomerUser(request);
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
	
	@PostMapping("/sendmobileOtp")
	public ResponseEntity<?> sendMobileOtp(@RequestBody CustomerRegisterEntity login) {
		String dlerMobileNo = login.getPhoneNumber();
		ResponseMessageDto message = new ResponseMessageDto();
		try {
			if (registerRepository.findByPhoneNumber(dlerMobileNo) == null) {
				message.setMessage("Invalid mobile number");
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else {
				Optional<CustomerRegisterEntity> user = registerRepository.findByPhoneNumber(dlerMobileNo);

				if (!user.get().getMobileVerify().equalsIgnoreCase("yes")) {
					otpHandler.generateMobileOtp(dlerMobileNo);
					message.setStatus(true);
					message.setMessage("Otp sent to the registered mobile number");
					return new ResponseEntity<>(message, HttpStatus.OK);
				}
				message.setMessage("Mobile number already verified");
				return new ResponseEntity<>(message, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}
	}
	
	@Transactional
	@PostMapping("/verifySmsOtp")
	public ResponseEntity<?> verifyMobileOtp(@RequestBody CustomerRegisterEntity login) {
		String mobile = login.getPhoneNumber();
		String otp = login.getMobileOtp();
		ResponseMessageDto message = new ResponseMessageDto();
		try {
			if (otpHandler.verifyMobileOtp(mobile, otp)) {

				customerHandler.updateDataWithMobile(otp, mobile);
				message.setStatus(true);
				message.setMessage("mobile Verified successful");
				return new ResponseEntity<>(message, HttpStatus.OK);

			}
			message.setMessage("Incorrect OTP, Please enter correct Otp");
			return new ResponseEntity<>(message, HttpStatus.OK);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> changeDlerPassword(@RequestBody ChangeForgotdto changePassword) {
		String email = changePassword.getEmail();
		String oldPassword = changePassword.getOldPassword();
		String newPassword = changePassword.getNewPassword();
		String confirmPassword = changePassword.getConfirmPassword();
		String mobileNo = changePassword.getMobileNo();
		ResponseMessageDto message = new ResponseMessageDto();
		try {
			String password = customerHandler.changePassword(email, oldPassword, newPassword,
					confirmPassword, mobileNo);
			if (password == "changed") {
				message.setMessage("Password Changed Successfully");
				message.setStatus(true);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else if (password == "notMatched") {
				message.setMessage("New Passwords Not Matched");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else if (password == "incorrect") {
				message.setMessage("Old Password is Incorrect");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			}
		} catch (Exception e) {
			message.setMessage(e.getMessage());
			message.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(message);
		}
		message.setMessage("Invalid dler");
		message.setStatus(false);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	@PostMapping("/customerforgetPassword/sendOtp")
	public ResponseEntity<?> sendOtpForgotPassword(@RequestBody ChangeForgotdto password) {
		String email = password.getEmail();
		String mobileNo = password.getMobileNo();
		ResponseMessageDto message = new ResponseMessageDto();
		try {
			if (email != null && mobileNo == null) {
				if (customerHandler.sendMail(email) != null) {
					message.setMessage("OTP Sent to Registered EmailId");
					message.setStatus(true);
					return new ResponseEntity<>(message, HttpStatus.OK);
				}
				message.setMessage("Invalid EmailId");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else {
				if (customerHandler.sendSms(mobileNo) != null) {
					message.setMessage("OTP Sent to Registered Mobile Number");
					message.setStatus(true);
					return new ResponseEntity<>(message, HttpStatus.OK);
				}
				message.setMessage("Invalid Mobile Number");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			}

		} catch (Exception e) {
			message.setMessage("Invalid EmailId");
			message.setStatus(false);
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
	}
	
	@PostMapping("/customer/forgetPassword/verification")
	public ResponseEntity<?> dlerForgotPasswordVerify(@RequestBody ChangeForgotdto forgotPwd) {
		String email = forgotPwd.getEmail();
		String otp = forgotPwd.getOtp();
		String newPassword = forgotPwd.getNewPassword();
		String confirmPassword = forgotPwd.getConfirmPassword();
		String mobile = forgotPwd.getMobileNo();
		ResponseMessageDto message = new ResponseMessageDto();

		try {
			String data = customerHandler.forgetPassword(email, otp, newPassword, confirmPassword,
					mobile);
			if (data == "changed") {
				message.setMessage("Password Changed Successfully");
				message.setStatus(true);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else if (data == "notMatched") {
				message.setMessage("New Passwords Not Matched");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else if (data == "incorrect") {
				message.setMessage("Invalid OTP");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else if (data == "incorrectEmail") {
				message.setMessage("Invalid Email ID");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else {
				message.setMessage("Invalid Mobile Number");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			}
		} catch (Exception e) {
			message.setMessage(e.getMessage());
			message.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(message);
		}
	}
	
	@PostMapping("/rxwala-customer-delete-account")
	public ResponseEntity<GenericResponse> servicePersonDeletAccoutn(@RequestBody DeleteAccountRequest accountRequest) {
		return new ResponseEntity<GenericResponse>(customerHandler.servicePersonDeletAccout(accountRequest),
				HttpStatus.CREATED);
	}
	
	@PutMapping("/customer-profile-update")
    public ResponseEntity<GenericResponse> updateCustomer(@RequestBody CustomerUpdateRequest request) {
        CustomerRegisterDto updatedCustomer = customerHandler.updateCustomerDetails(request);

        GenericResponse response = new GenericResponse();
        response.setResponseMessage("Customer details updated successfully");
        response.setDetails(updatedCustomer);

        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/get-customer-info")
	public ResponseEntity<ResponseGetCustomerRegistrationDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String name, @RequestParam(required = false) String email,
			@RequestParam(required = false) String phoneNumber,@RequestParam(required = false) String cId,@RequestParam(required = false) String userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<CustomerRegistrationInfoDto> srpqPage = customerHandler.getCustomerInfo(name,
				email, phoneNumber,cId,userId,pageable);
		ResponseGetCustomerRegistrationDto response = new ResponseGetCustomerRegistrationDto();

		response.setMessage("customer details retrieved successfully.");
		response.setStatus(true);
		response.setCustomerRegistrationInfo(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/walk-in-customer")
	public ResponseEntity<?> addWalkInCustomer(@RequestBody WalkInCustomerRequestDto dlerBusinessl) {
		ResponseCustomerLoginDto response = new ResponseCustomerLoginDto();
		try {
			CustomerRegisterDto dblDTO = customerHandler.addWalkInCustomerRegistration(dlerBusinessl);
			if (dblDTO != null) {

				response.setMessage("Customer added successfully");
				response.setStatus(true);
				response.setDlerProfile(dblDTO);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("profile already exists");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			response.setMessage("failed to add");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}
	
	@PutMapping("/update-walk-in-customer")
	public ResponseEntity<com.kosuri.stores.model.dto.GenericResponse<CustomerWalkInUpdateRequest>> updateWalkInCustomer(@RequestBody CustomerWalkInUpdateRequest request) {
	    try {
	        CustomerWalkInUpdateRequest updatedCustomer = customerHandler.updateCustomer(request);

	        com.kosuri.stores.model.dto.GenericResponse<CustomerWalkInUpdateRequest> response = new com.kosuri.stores.model.dto.GenericResponse<>(
	            "success",
	            "Customer updated successfully",
	            updatedCustomer
	        );

	        return ResponseEntity.ok(response);
	    } catch (RuntimeException ex) {
	        com.kosuri.stores.model.dto.GenericResponse<CustomerWalkInUpdateRequest> errorResponse = new com.kosuri.stores.model.dto.GenericResponse<>(
	            "error",
	            ex.getMessage(),
	            null
	        );

	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	    }
	}


}
